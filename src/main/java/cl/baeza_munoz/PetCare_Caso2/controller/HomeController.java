package cl.baeza_munoz.PetCare_Caso2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import cl.baeza_munoz.PetCare_Caso2.service.ClienteService;
import cl.baeza_munoz.PetCare_Caso2.service.MascotaService;
import cl.baeza_munoz.PetCare_Caso2.service.ReservaService;
import cl.baeza_munoz.PetCare_Caso2.service.TratamientoService;
import cl.baeza_munoz.PetCare_Caso2.service.VeterinarioService;

@Controller
public class HomeController {

    private final ClienteService clienteService;
    private final MascotaService mascotaService;
    private final VeterinarioService veterinarioService;
    private final ReservaService reservaService;
    private final TratamientoService tratamientoService;

    public HomeController(ClienteService clienteService,
                          MascotaService mascotaService,
                          VeterinarioService veterinarioService,
                          ReservaService reservaService,
                          TratamientoService tratamientoService) {
        this.clienteService = clienteService;
        this.mascotaService = mascotaService;
        this.veterinarioService = veterinarioService;
        this.reservaService = reservaService;
        this.tratamientoService = tratamientoService;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("totalClientes", clienteService.contar());
        model.addAttribute("totalMascotas", mascotaService.contar());
        model.addAttribute("totalVeterinarios", veterinarioService.contar());
        model.addAttribute("totalReservas", reservaService.contar());
        model.addAttribute("totalTratamientos", tratamientoService.contar());
        model.addAttribute("mascotas", mascotaService.listar(null, null).stream().limit(6).toList());
        model.addAttribute("veterinarios", veterinarioService.listar());
        model.addAttribute("reservasRecientes", reservaService.listar(null, null, null, null).stream().limit(5).toList());
        return "index";
    }
}
