package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.AutoPackagingRecord
import com.example.data.local.entities.BagType
import com.example.data.local.entities.Client
import com.example.data.local.entities.Department
import com.example.data.local.entities.InjectionRecord
import com.example.data.local.entities.ManualPackagingRecord
import com.example.data.local.entities.Machine
import com.example.data.local.entities.Shift
import com.example.data.local.entities.Worker
import kotlinx.coroutines.flow.Flow

@Dao
interface FactoryDao {

    // === Clients ===
    @Query("SELECT * FROM clients ORDER BY name ASC")
    fun getAllClients(): Flow<List<Client>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: Client): Long

    @Update
    suspend fun updateClient(client: Client)

    @Delete
    suspend fun deleteClient(client: Client)

    // === Bag Types ===
    @Query("SELECT * FROM bag_types ORDER BY clientName ASC, typeName ASC")
    fun getAllBagTypes(): Flow<List<BagType>>

    @Query("SELECT * FROM bag_types WHERE clientId = :clientId")
    fun getBagTypesForClient(clientId: Int): Flow<List<BagType>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBagType(bagType: BagType): Long

    @Update
    suspend fun updateBagType(bagType: BagType)

    @Delete
    suspend fun deleteBagType(bagType: BagType)

    // === Departments ===
    @Query("SELECT * FROM departments ORDER BY name ASC")
    fun getAllDepartments(): Flow<List<Department>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDepartment(department: Department): Long

    @Update
    suspend fun updateDepartment(department: Department)

    @Delete
    suspend fun deleteDepartment(department: Department)

    // === Machines ===
    @Query("SELECT * FROM machines ORDER BY name ASC")
    fun getAllMachines(): Flow<List<Machine>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMachine(machine: Machine): Long

    @Update
    suspend fun updateMachine(machine: Machine)

    @Delete
    suspend fun deleteMachine(machine: Machine)

    // === Shifts ===
    @Query("SELECT * FROM shifts ORDER BY id ASC")
    fun getAllShifts(): Flow<List<Shift>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShift(shift: Shift): Long

    @Update
    suspend fun updateShift(shift: Shift)

    @Delete
    suspend fun deleteShift(shift: Shift)

    // === Workers ===
    @Query("SELECT * FROM workers ORDER BY name ASC")
    fun getAllWorkers(): Flow<List<Worker>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorker(worker: Worker): Long

    @Update
    suspend fun updateWorker(worker: Worker)

    @Delete
    suspend fun deleteWorker(worker: Worker)

    // === Module A: Injection Records ===
    @Query("SELECT * FROM injection_records ORDER BY timestamp DESC")
    fun getAllInjectionRecords(): Flow<List<InjectionRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInjectionRecord(record: InjectionRecord): Long

    @Delete
    suspend fun deleteInjectionRecord(record: InjectionRecord)

    // === Module B: Auto Packaging Records ===
    @Query("SELECT * FROM auto_packaging_records ORDER BY timestamp DESC")
    fun getAllAutoPackagingRecords(): Flow<List<AutoPackagingRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAutoPackagingRecord(record: AutoPackagingRecord): Long

    @Delete
    suspend fun deleteAutoPackagingRecord(record: AutoPackagingRecord)

    // === Module C: Manual Packaging Records ===
    @Query("SELECT * FROM manual_packaging_records ORDER BY timestamp DESC")
    fun getAllManualPackagingRecords(): Flow<List<ManualPackagingRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManualPackagingRecord(record: ManualPackagingRecord): Long

    @Delete
    suspend fun deleteManualPackagingRecord(record: ManualPackagingRecord)
}
