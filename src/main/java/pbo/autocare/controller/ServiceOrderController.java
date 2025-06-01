// pbo.autocare.controller.ServiceOrderController.java
package pbo.autocare.controller;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

// ... (imports lainnya)
//import pbo.autocare.service.ServiceOrderService; // <--- Anda meng-import interface-nya di sini

@Controller
@RequestMapping("/serviceorders")
public class ServiceOrderController {

    //@Autowired
    //private ServiceOrderService serviceOrderService; // <--- Anda menyuntikkan interface-nya

    // ... (metode-metode controller)
}