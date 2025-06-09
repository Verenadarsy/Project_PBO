package pbo.autocare.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import pbo.autocare.model.ServiceItem;
import pbo.autocare.model.ServiceOrder;
import pbo.autocare.model.Technician;
import pbo.autocare.model.Transaction; // Import Transaction
import pbo.autocare.model.User;
import pbo.autocare.model.Vehicle;
import pbo.autocare.repository.ServiceItemRepository;
import pbo.autocare.repository.ServiceOrderRepository;
import pbo.autocare.repository.ServiceRepository;
import pbo.autocare.repository.TechnicianRepository;
import pbo.autocare.repository.TransactionRepository; // Import TransactionRepository
import pbo.autocare.repository.UserRepository;
import pbo.autocare.repository.VehicleRepository;

@Service
public class ServiceOrderServiceImpl implements ServiceOrderService {

    @Autowired
    private ServiceOrderRepository serviceOrderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private TransactionRepository transactionRepository; // Injeksi TransactionRepository

    @Autowired
    private TechnicianRepository technicianRepository;

    @Autowired
    private ServiceItemRepository serviceItemRepository; // Injeksi ServiceItemRepository
    
    // Injeksi TechnicianRepository

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Override
    public List<ServiceItem> getAllServices() {
        return serviceRepository.findAll();
    }

    @Override
    public Optional<ServiceOrder> findById(Long id) {
        return serviceOrderRepository.findById(id);
    }

    @Override
    public List<ServiceOrder> findAll() {
        return serviceOrderRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        // Penting: Jika Transaction memiliki FK non-nullable ke ServiceOrder,
        // Anda harus mengelola penghapusan transaksi terkait di sini atau
        // menggunakan CascadeType.ALL pada relasi @OneToOne di ServiceOrder.
        // Contoh: transactionRepository.deleteByServiceOrderId(id); (jika metode ada)
        serviceOrderRepository.deleteById(id);
    }

    /**
     * Menyimpan ServiceOrder baru atau memperbarui yang sudah ada.
     * Untuk ServiceOrder baru, secara otomatis membuat Transaction dengan status PENDING.
     *
     * @param serviceOrder objek ServiceOrder yang akan disimpan.
     * @return ServiceOrder yang telah disimpan.
     */
    @Transactional
    @Override
    public ServiceOrder saveServiceOrder(ServiceOrder serviceOrder) {
        boolean isNewOrder = serviceOrder.getId() == null;

        if (isNewOrder) {
            serviceOrder.setOrderStatus(ServiceOrder.OrderStatus.PENDING);
            serviceOrder.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        }
        serviceOrder.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        // 1. Simpan ServiceOrder terlebih dahulu untuk mendapatkan ID yang dihasilkan (jika baru)
        ServiceOrder savedServiceOrder = serviceOrderRepository.save(serviceOrder);

        // 2. Jika ini adalah order baru, buat transaksi terkait dengan status PENDING
        if (isNewOrder) {
            Transaction newTransaction = new Transaction();
            newTransaction.setServiceOrder(savedServiceOrder); // Hubungkan ke ServiceOrder yang baru disimpan
            newTransaction.setAmount(savedServiceOrder.getFinalPrice()); // Ambil amount dari finalPrice ServiceOrder
            newTransaction.setTransactionDate(new Timestamp(System.currentTimeMillis()));
            // Payment method akan disetel ke "Unspecified" secara default saat transaksi baru dibuat
            newTransaction.setPaymentMethod("Unspecified");
            newTransaction.setTransactionStatus(Transaction.TransactionStatus.PENDING); // Set status PENDING
            newTransaction.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            newTransaction.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            transactionRepository.save(newTransaction);
        } else {
            // Logika update Transaction terkait jika finalPrice berubah saat ServiceOrder diupdate
            // Ini bersifat opsional dan tergantung pada kebutuhan bisnis Anda
            // Jika Anda menambahkan @OneToOne(mappedBy = "serviceOrder") di ServiceOrder.java,
            // Anda bisa mengakses transaksi terkait langsung dari order.
            // Contoh:
            // if (savedServiceOrder.getTransaction() != null && savedServiceOrder.getTransaction().getAmount().compareTo(savedServiceOrder.getFinalPrice()) != 0) {
            //     Transaction existingTransaction = savedServiceOrder.getTransaction();
            //     existingTransaction.setAmount(savedServiceOrder.getFinalPrice());
            //     existingTransaction.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            //     transactionRepository.save(existingTransaction);
            // }
        }

        return savedServiceOrder;
    }

    @Override
    public void saveOrder(ServiceOrder serviceOrder) {
        saveServiceOrder(serviceOrder);
    }

    @Override
    public long countOrdersThisMonth() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth())
                                         .withHour(0).withMinute(0).withSecond(0).withNano(0);
        Timestamp startOfMonthTimestamp = Timestamp.from(startOfMonth.atZone(ZoneId.systemDefault()).toInstant());

        return serviceOrderRepository.countByCreatedAtAfter(startOfMonthTimestamp);
    }

    @Override
    public List<ServiceOrder> getAllServiceOrders() {
        return serviceOrderRepository.findAll();
    }

    @Override
    public Optional<ServiceOrder> getServiceOrderById(Long id) {
        return serviceOrderRepository.findById(id);
    }

    @Transactional
    @Override
    public ServiceOrder updateServiceOrder(Long id, ServiceOrder updatedServiceOrder) {
        return serviceOrderRepository.findById(id)
                .map(existingOrder -> {
                    existingOrder.setUser(updatedServiceOrder.getUser());
                    existingOrder.setCustomerName(updatedServiceOrder.getCustomerName());
                    existingOrder.setCustomerContact(updatedServiceOrder.getCustomerContact());
                    existingOrder.setCustomerAddress(updatedServiceOrder.getCustomerAddress());
                    existingOrder.setVehicleModelName(updatedServiceOrder.getVehicleModelName());
                    existingOrder.setVehicleType(updatedServiceOrder.getVehicleType());
                    existingOrder.setLicensePlate(updatedServiceOrder.getLicensePlate());
                    existingOrder.setService(updatedServiceOrder.getService());
                    existingOrder.setFinalPrice(updatedServiceOrder.getFinalPrice());
                    existingOrder.setSelectedDurationDays(updatedServiceOrder.getSelectedDurationDays());
                    existingOrder.setServiceName(updatedServiceOrder.getServiceName());
                    existingOrder.setOrderStatus(updatedServiceOrder.getOrderStatus());
                    existingOrder.setOrderNotes(updatedServiceOrder.getOrderNotes());

                    // Panggil saveServiceOrder untuk menangani logika update dan potensi update transaksi
                    return saveServiceOrder(existingOrder);
                }).orElseThrow(() -> new RuntimeException("Service Order not found with id " + id));
    }

    public void deleteServiceOrder(Long id) {
        serviceOrderRepository.deleteById(id);
    }

    public void createServiceOrder(ServiceOrder serviceOrder) {
    serviceOrderRepository.save(serviceOrder); // Call save, but don't return its result
    }

    public Optional<ServiceOrder> assignTechnicianToOrder(Long orderId, Long technicianId) {
        Optional<ServiceOrder> orderOptional = serviceOrderRepository.findById(orderId);
        Optional<Technician> technicianOptional = technicianRepository.findById(technicianId);

        if (orderOptional.isPresent() && technicianOptional.isPresent()) {
            ServiceOrder order = orderOptional.get();
            Technician technician = technicianOptional.get();
            order.setAssignedTechnician(technician);
            order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            return Optional.of(serviceOrderRepository.save(order));
        }
        return Optional.empty();
    }

    public List<Technician> getAllTechnicians() {
        return technicianRepository.findAll();
    }

    /**
     * Mendapatkan daftar teknisi yang memiliki spesialisasi sesuai dengan yang dibutuhkan oleh ServiceItem.
     * Menggunakan relasi `requiredSpecialization` di ServiceItem dan `specialization` di Technician.
     * @param serviceItemId ID dari ServiceItem yang dipesan
     * @return Daftar teknisi yang memiliki spesialisasi yang cocok, atau daftar kosong jika tidak ada atau ServiceItem tidak ditemukan.
     */
    public List<Technician> getTechniciansByServiceSpecialization(Long serviceItemId) {
        Optional<ServiceItem> serviceItemOptional = serviceItemRepository.findById(serviceItemId);

        if (serviceItemOptional.isPresent()) {
            ServiceItem serviceItem = serviceItemOptional.get();
            // Periksa apakah ServiceItem ini memang membutuhkan spesialisasi
            if (serviceItem.getRequiredSpecialization() != null && serviceItem.getRequiredSpecialization().getCode() != null) {
                String requiredCode = serviceItem.getRequiredSpecialization().getCode();
                // Cari teknisi yang memiliki spesialisasi dengan kode tersebut
                return technicianRepository.findBySpecialization_Code(requiredCode);
            }
            // Jika ServiceItem tidak membutuhkan spesialisasi atau kodenya null, kembalikan semua teknisi
            // Atau Anda bisa memutuskan untuk mengembalikan daftar kosong jika tidak ada spesialisasi wajib.
            // Untuk kasus ini, saya akan kembalikan semua teknisi jika tidak ada spesialisasi wajib.
            return technicianRepository.findAll(); // Atau Collections.emptyList() jika tidak ada spesialisasi wajib
        }
        return Collections.emptyList(); // Jika ServiceItem tidak ditemukan
    }

    @Override // Ini opsional, tapi membantu menandakan override method dari interface/superclass
    public Optional<ServiceOrder> updateOrderStatus(Long id, ServiceOrder.OrderStatus newStatus) {
        // Cari order berdasarkan ID
        return serviceOrderRepository.findById(id)
                .map(order -> {
                    // Jika order ditemukan, update statusnya
                    order.setOrderStatus(newStatus);
                    // Update timestamp perubahan
                    order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                    // Simpan perubahan ke database
                    return serviceOrderRepository.save(order);
                });
        // Jika order tidak ditemukan, .map() akan mengembalikan Optional.empty()
    }
}