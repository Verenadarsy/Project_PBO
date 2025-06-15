package pbo.autocare.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AdminServiceOrderFormDTO {

    private Long id;

    @NotNull(message = "User harus dipilih")
    private Long userId;

    @NotBlank(message = "Nama customer tidak boleh kosong")
    @Size(min = 3, max = 100, message = "Nama customer harus antara 3 dan 100 karakter")
    private String customerName;

    @NotBlank(message = "Kontak customer tidak boleh kosong")
    @Size(min = 10, max = 15, message = "Kontak customer harus antara 10 dan 15 digit")
    private String customerContact;

    private String customerAddress; 

    @NotBlank(message = "Model kendaraan tidak boleh kosong")
    private String vehicleModelName;

    @NotNull(message = "Jenis kendaraan harus dipilih")
    private Integer vehicleTypeId; 

    @NotBlank(message = "Plat nomor tidak boleh kosong")
    @Size(min = 3, max = 20, message = "Plat nomor harus antara 3 dan 20 karakter")
    private String licensePlate;

    @NotNull(message = "Layanan harus dipilih")
    private Long serviceId;

    @NotBlank(message = "Nama servis tidak boleh kosong")
    private String serviceName; 

    @NotNull(message = "Durasi hari diperlukan.")
    @Min(value = 0, message = "Durasi minimal 0 hari.") 
    private Integer selectedDurationDays;

    @Min(value = 0, message = "Harga akhir tidak boleh negatif")
    private BigDecimal finalPrice; 

    private String orderNotes;

    public AdminServiceOrderFormDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerContact() { return customerContact; }
    public void setCustomerContact(String customerContact) { this.customerContact = customerContact; }
    public String getCustomerAddress() { return customerAddress; }
    public void setCustomerAddress(String customerAddress) { this.customerAddress = customerAddress; }
    public String getVehicleModelName() { return vehicleModelName; }
    public void setVehicleModelName(String vehicleModelName) { this.vehicleModelName = vehicleModelName; }
    public Integer getVehicleTypeId() { return vehicleTypeId; }
    public void setVehicleTypeId(Integer vehicleTypeId) { this.vehicleTypeId = vehicleTypeId; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public Integer getSelectedDurationDays() { return selectedDurationDays; }
    public void setSelectedDurationDays(Integer selectedDurationDays) { this.selectedDurationDays = selectedDurationDays; }
    public BigDecimal getFinalPrice() { return finalPrice; }
    public void setFinalPrice(BigDecimal finalPrice) { this.finalPrice = finalPrice; }
    public String getOrderNotes() { return orderNotes; }
    public void setOrderNotes(String orderNotes) { this.orderNotes = orderNotes; }
}