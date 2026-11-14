package cl.baeza_munoz.PetCare_Caso2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import cl.baeza_munoz.PetCare_Caso2.service.ClienteService;
import cl.baeza_munoz.PetCare_Caso2.service.MascotaService;
import cl.baeza_munoz.PetCare_Caso2.service.VeterinarioService;

@Controller
public class HomeController {

    private final ClienteService clienteService;
    private final MascotaService mascotaService;
    private final VeterinarioService veterinarioService;

    public HomeController(ClienteService clienteService, MascotaService mascotaService,
                          VeterinarioService veterinarioService) {
        this.clienteService = clienteService;
        this.mascotaService = mascotaService;
        this.veterinarioService = veterinarioService;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("totalClientes", clienteService.contar());
        model.addAttribute("totalMascotas", mascotaService.contar());
        model.addAttribute("totalVeterinarios", veterinarioService.contar());
        model.addAttribute("mascotas", mascotaService.listar(null, null).stream().limit(6).toList());
        model.addAttribute("veterinarios", veterinarioService.listar());
        return "index";
    }
}
