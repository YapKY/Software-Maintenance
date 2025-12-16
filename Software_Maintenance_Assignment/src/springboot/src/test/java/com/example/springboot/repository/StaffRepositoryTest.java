package com.example.springboot.repository;

import com.example.springboot.model.Staff;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StaffRepositoryTest {

    @Mock
    private FirestoreRepository firestoreRepository;

    @Mock
    private Firestore firestore;

    @Mock
    private CollectionReference collectionReference;

    @Mock
    private DocumentReference documentReference;

    @Mock
    private ApiFuture<WriteResult> writeResultFuture;

    @Mock
    private ApiFuture<QuerySnapshot> querySnapshotFuture;

    @Mock
    private QuerySnapshot querySnapshot;

    @Mock
    private DocumentSnapshot documentSnapshot;

    @Mock
    private QueryDocumentSnapshot queryDocumentSnapshot;

    @Mock
    private Query query;

    @InjectMocks
    private StaffRepository staffRepository;

    private Staff staff;

    @BeforeEach
    void setUp() throws Exception {
        staff = new Staff();
        staff.setStaffId("S001");
        staff.setName("Test Staff");
        staff.setPosition("Manager");
        staff.setEmail("staff@example.com");

        when(firestoreRepository.getFirestore()).thenReturn(firestore);
        when(firestore.collection("staff")).thenReturn(collectionReference);
        when(collectionReference.document(anyString())).thenReturn(documentReference);
    }

    @Test
    void testFindByStaffId_Found() throws Exception {
        when(firestoreRepository.findById("staff", "S001", Staff.class)).thenReturn(staff);

        Staff result = staffRepository.findByStaffId("S001");

        assertNotNull(result);
        assertEquals("S001", result.getStaffId());
    }

    @Test
    void testFindByStaffId_NotFound() throws Exception {
        when(firestoreRepository.findById("staff", "S001", Staff.class)).thenReturn(null);

        Staff result = staffRepository.findByStaffId("S001");

        assertNull(result);
    }

    @Test
    void testFindAll() throws ExecutionException, InterruptedException {
        when(collectionReference.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Collections.singletonList(queryDocumentSnapshot));

        // Mock documentToStaff conversion
        when(queryDocumentSnapshot.getId()).thenReturn("S001");
        when(queryDocumentSnapshot.getString("staffId")).thenReturn("S001");
        when(queryDocumentSnapshot.getString("name")).thenReturn("Test Staff");

        List<Staff> result = staffRepository.findAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("S001", result.get(0).getStaffId());
    }

    @Test
    void testSave() {
        doNothing().when(firestoreRepository).saveWithId(anyString(), anyString(), any());

        Staff savedStaff = staffRepository.save(staff);

        assertEquals("S001", savedStaff.getDocumentId());
        verify(firestoreRepository).saveWithId("staff", "S001", staff);
    }

    @Test
    void testUpdate() throws ExecutionException, InterruptedException {
        when(documentReference.set(anyMap(), any(SetOptions.class))).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(null);

        staffRepository.update("S001", staff);

        verify(documentReference).set(anyMap(), eq(SetOptions.merge()));
    }

    @Test
    void testDelete() throws ExecutionException, InterruptedException {
        when(documentReference.delete()).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(null);

        staffRepository.delete("S001");

        verify(documentReference).delete();
    }

    @Test
    void testExistsByStaffId_True() throws Exception {
        when(firestoreRepository.findById("staff", "S001", Staff.class)).thenReturn(staff);

        boolean exists = staffRepository.existsByStaffId("S001");

        assertTrue(exists);
    }

    @Test
    void testExistsByStaffId_False() throws Exception {
        when(firestoreRepository.findById("staff", "S001", Staff.class)).thenReturn(null);

        boolean exists = staffRepository.existsByStaffId("S001");

        assertFalse(exists);
    }

    @Test
    void testFindByPosition() throws ExecutionException, InterruptedException {
        when(firestoreRepository.getCollectionByField("staff", "position", "Manager")).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Collections.singletonList(queryDocumentSnapshot));

        when(queryDocumentSnapshot.getId()).thenReturn("S001");
        when(queryDocumentSnapshot.getString("staffId")).thenReturn("S001");
        when(queryDocumentSnapshot.getString("position")).thenReturn("Manager");

        List<Staff> result = staffRepository.findByPosition("Manager");

        assertFalse(result.isEmpty());
        assertEquals("Manager", result.get(0).getPosition());
    }
}
