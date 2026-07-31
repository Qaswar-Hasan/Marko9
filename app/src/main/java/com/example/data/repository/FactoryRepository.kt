package com.example.data.repository

import com.example.data.local.dao.FactoryDao
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

class FactoryRepository(private val dao: FactoryDao) {

    // Clients
    val clients: Flow<List<Client>> = dao.getAllClients()
    suspend fun insertClient(client: Client) = dao.insertClient(client)
    suspend fun updateClient(client: Client) = dao.updateClient(client)
    suspend fun deleteClient(client: Client) = dao.deleteClient(client)

    // Bag Types
    val bagTypes: Flow<List<BagType>> = dao.getAllBagTypes()
    suspend fun insertBagType(bagType: BagType) = dao.insertBagType(bagType)
    suspend fun updateBagType(bagType: BagType) = dao.updateBagType(bagType)
    suspend fun deleteBagType(bagType: BagType) = dao.deleteBagType(bagType)

    // Departments
    val departments: Flow<List<Department>> = dao.getAllDepartments()
    suspend fun insertDepartment(department: Department) = dao.insertDepartment(department)
    suspend fun updateDepartment(department: Department) = dao.updateDepartment(department)
    suspend fun deleteDepartment(department: Department) = dao.deleteDepartment(department)

    // Machines
    val machines: Flow<List<Machine>> = dao.getAllMachines()
    suspend fun insertMachine(machine: Machine) = dao.insertMachine(machine)
    suspend fun updateMachine(machine: Machine) = dao.updateMachine(machine)
    suspend fun deleteMachine(machine: Machine) = dao.deleteMachine(machine)

    // Shifts
    val shifts: Flow<List<Shift>> = dao.getAllShifts()
    suspend fun insertShift(shift: Shift) = dao.insertShift(shift)
    suspend fun updateShift(shift: Shift) = dao.updateShift(shift)
    suspend fun deleteShift(shift: Shift) = dao.deleteShift(shift)

    // Workers
    val workers: Flow<List<Worker>> = dao.getAllWorkers()
    suspend fun insertWorker(worker: Worker) = dao.insertWorker(worker)
    suspend fun updateWorker(worker: Worker) = dao.updateWorker(worker)
    suspend fun deleteWorker(worker: Worker) = dao.deleteWorker(worker)

    // Module A: Injection
    val injectionRecords: Flow<List<InjectionRecord>> = dao.getAllInjectionRecords()
    suspend fun insertInjectionRecord(record: InjectionRecord) = dao.insertInjectionRecord(record)
    suspend fun deleteInjectionRecord(record: InjectionRecord) = dao.deleteInjectionRecord(record)

    // Module B: Auto Packaging
    val autoPackagingRecords: Flow<List<AutoPackagingRecord>> = dao.getAllAutoPackagingRecords()
    suspend fun insertAutoPackagingRecord(record: AutoPackagingRecord) = dao.insertAutoPackagingRecord(record)
    suspend fun deleteAutoPackagingRecord(record: AutoPackagingRecord) = dao.deleteAutoPackagingRecord(record)

    // Module C: Manual Packaging
    val manualPackagingRecords: Flow<List<ManualPackagingRecord>> = dao.getAllManualPackagingRecords()
    suspend fun insertManualPackagingRecord(record: ManualPackagingRecord) = dao.insertManualPackagingRecord(record)
    suspend fun deleteManualPackagingRecord(record: ManualPackagingRecord) = dao.deleteManualPackagingRecord(record)
}
