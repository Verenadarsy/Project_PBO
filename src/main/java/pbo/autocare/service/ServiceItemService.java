package pbo.autocare.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- Gunakan ini untuk Spring Transactional

import pbo.autocare.model.ServiceItem;
import pbo.autocare.model.Specialization;
import pbo.autocare.model.Generalservice; // Pastikan semua subclass yang relevan diimport
import pbo.autocare.model.SpecializedService;
import pbo.autocare.repository.ServiceItemRepository;
import pbo.autocare.repository.SpecializationRepository;

@Service
public class ServiceItemService {

    private final ServiceItemRepository serviceItemRepository;
    private final SpecializationRepository specializationRepository; // Deklarasi di sini

    // Direkomendasikan menggunakan Constructor Injection untuk @Autowired
    @Autowired
    public ServiceItemService(ServiceItemRepository serviceItemRepository,
                              SpecializationRepository specializationRepository) {
        this.serviceItemRepository = serviceItemRepository;
        this.specializationRepository = specializationRepository;
    }

    // Mengambil semua Service Item
    @Transactional(readOnly = true) // <-- Anotasi transaksi untuk operasi baca
    public List<ServiceItem> getAllServiceItems() {
        return serviceItemRepository.findAll();
    }

    // Mengambil Service Item berdasarkan ID
    @Transactional(readOnly = true) // <-- Anotasi transaksi untuk operasi baca
    public Optional<ServiceItem> getServiceItemById(Long id) {
        return serviceItemRepository.findById(id);
    }

    // Metode untuk menyimpan (create/update) Service Item
    // Ini adalah metode utama yang akan dipanggil dari controller untuk menyimpan instance ServiceItem
    // (baik itu Generalservice atau SpecializedService)
    @Transactional // <-- Anotasi transaksi untuk operasi tulis
    public ServiceItem saveServiceItem(ServiceItem serviceItem) {
        // Jika Anda menggunakan @PrePersist dan @PreUpdate di ServiceItem, Anda tidak perlu setting timestamp di sini.
        // Jika tidak, Anda bisa setting di sini:
        // if (serviceItem.getId() == null) { // New item
        //     serviceItem.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        // }
        // serviceItem.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return serviceItemRepository.save(serviceItem);
    }

    // Metode untuk memperbarui Service Item yang sudah ada
    @Transactional // <-- Anotasi transaksi untuk operasi tulis
    public ServiceItem updateServiceItem(Long id, ServiceItem updatedServiceItem) {
        // Cari item yang sudah ada di database
        ServiceItem existingItem = serviceItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service Item tidak ditemukan dengan ID: " + id));

        // Perbarui properti umum yang ada di ServiceItem base class
        existingItem.setServiceName(updatedServiceItem.getServiceName());
        existingItem.setServiceCategory(updatedServiceItem.getServiceCategory());
        existingItem.setBasePrice(updatedServiceItem.getBasePrice());
        existingItem.setRequiredSpecialization(updatedServiceItem.getRequiredSpecialization());

        // Logika untuk memperbarui properti spesifik subclass
        // PENTING: Dengan strategi SINGLE_TABLE, JPA tidak bisa langsung mengubah jenis subclass yang sudah ada.
        // Asumsikan jenis layanan (General/Spesialis) tidak berubah setelah dibuat.
        // Jika berubah, Anda harus menghapus yang lama dan membuat yang baru di controller.
        if (existingItem instanceof Generalservice && updatedServiceItem instanceof Generalservice) {
            Generalservice existingGeneral = (Generalservice) existingItem;
            Generalservice incomingGeneral = (Generalservice) updatedServiceItem;
            existingGeneral.setGeneralDurationDaysMin(incomingGeneral.getGeneralDurationDaysMin());
            existingGeneral.setGeneralDurationDaysMax(incomingGeneral.getGeneralDurationDaysMax());
            existingGeneral.setSpecialDurationDaysMin(0); // Set ke 0 karena ini Generalservice
            existingGeneral.setSpecialDurationDaysMax(0); // Set ke 0 karena ini Generalservice
        } else if (existingItem instanceof SpecializedService && updatedServiceItem instanceof SpecializedService) {
            SpecializedService existingSpecialized = (SpecializedService) existingItem;
            SpecializedService incomingSpecialized = (SpecializedService) updatedServiceItem;
            existingSpecialized.setSpecialDurationDaysMin(incomingSpecialized.getSpecialDurationDaysMin());
            existingSpecialized.setSpecialDurationDaysMax(incomingSpecialized.getSpecialDurationDaysMax());
            existingSpecialized.setGeneralDurationDaysMin(0); // Set ke 0 karena ini SpecializedService
            existingSpecialized.setGeneralDurationDaysMax(0); // Set ke 0 karena ini SpecializedService
        } else {
            // Ini akan terpicu jika ada upaya untuk mengubah tipe layanan atau jika objek yang dikirim
            // dari form tidak cocok dengan tipe yang ada di database.
            // Anda bisa melempar pengecualian atau menangani kasus ini sesuai kebutuhan bisnis Anda.
            throw new IllegalArgumentException("Tidak dapat memperbarui layanan: Tipe layanan tidak sesuai dengan yang tersimpan.");
        }

        return serviceItemRepository.save(existingItem);
    }

    // Menghapus Service Item
    @Transactional // <-- Anotasi transaksi untuk operasi tulis
    public void deleteServiceItem(Long id) {
        serviceItemRepository.deleteById(id);
    }

    // Mengambil semua Spesialisasi (untuk dropdown di form layanan)
    @Transactional(readOnly = true) // <-- Anotasi transaksi untuk operasi baca
    public List<Specialization> getAllSpecializations() {
        return specializationRepository.findAll();
    }

    // ----------- METODE REDUNDAN YANG TELAH DIHAPUS/DIGABUNGKAN -----------
    // public void saveServiceItem(SpecializedService specializedService) { ... }
    // public void saveServiceItem(ServiceItem existingServiceItem) { ... }
    // Metode ini sudah digantikan oleh 'saveServiceItem(ServiceItem serviceItem)' di atas.
    // Anotasi @Transactional dari 'jakarta.transaction' diubah menjadi 'org.springframework.transaction.annotation.Transactional'
    // agar konsisten dengan praktik Spring Boot umum.
}