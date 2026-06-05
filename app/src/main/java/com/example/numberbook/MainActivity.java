package com.example.numberbook;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Button btnFetch, btnUpload, btnSearch;
    private EditText searchInput;
    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private List<Contact> localContacts = new ArrayList<>();
    private ContactApi apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialization
        initViews();
        setupRecyclerView();
        
        apiService = RetrofitClient.getInstance().create(ContactApi.class);

        // Click listeners
        btnFetch.setOnClickListener(v -> checkAndRequestContactsPermission());
        btnUpload.setOnClickListener(v -> startCloudSync());
        btnSearch.setOnClickListener(v -> executeRemoteSearch());
    }

    private void initViews() {
        btnFetch = findViewById(R.id.btnFetchContacts);
        btnUpload = findViewById(R.id.btnUploadToServer);
        btnSearch = findViewById(R.id.btnDoSearch);
        searchInput = findViewById(R.id.editSearchQuery);
        recyclerView = findViewById(R.id.rvContacts);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactAdapter = new ContactAdapter(localContacts);
        recyclerView.setAdapter(contactAdapter);
    }

    private void checkAndRequestContactsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            importDeviceContacts();
        } else {
            permissionRequestLauncher.launch(Manifest.permission.READ_CONTACTS);
        }
    }

    private final ActivityResultLauncher<String> permissionRequestLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    importDeviceContacts();
                } else {
                    Toast.makeText(this, "Accès aux contacts refusé", Toast.LENGTH_SHORT).show();
                }
            });

    private void importDeviceContacts() {
        localContacts.clear();

        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        );

        if (cursor != null) {
            int nameIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int numberIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);

            while (cursor.moveToNext()) {
                String name = cursor.getString(nameIndex);
                String phone = cursor.getString(numberIndex);
                localContacts.add(new Contact(name, phone));
            }
            cursor.close();
        }

        contactAdapter.refreshList(localContacts);
        Toast.makeText(this, localContacts.size() + " contacts importés", Toast.LENGTH_SHORT).show();
    }

    private void startCloudSync() {
        if (localContacts.isEmpty()) {
            Toast.makeText(this, "Aucun contact à synchroniser. Importez-les d'abord.", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Contact c : localContacts) {
            apiService.postNewContact(c).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                    // Success silently for each contact in this simple version
                }

                @Override
                public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                    // Handle failure if needed
                }
            });
        }

        Toast.makeText(this, "Processus de synchronisation démarré...", Toast.LENGTH_SHORT).show();
    }

    private void executeRemoteSearch() {
        String query = searchInput.getText().toString().trim();

        if (query.isEmpty()) {
            Toast.makeText(this, "Veuillez entrer un nom ou un numéro", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.queryContacts(query).enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(@NonNull Call<List<Contact>> call, @NonNull Response<List<Contact>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    contactAdapter.refreshList(response.body());
                    Toast.makeText(MainActivity.this, response.body().size() + " résultats trouvés", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Contact>> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Échec de la recherche : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
