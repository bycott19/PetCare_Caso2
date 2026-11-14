package cl.baeza_munoz.PetCare_Caso2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cl.baeza_munoz.PetCare_Caso2.dto.ClienteDto;
import cl.baeza_munoz.PetCare_Caso2.exception.ConflictoException;
import cl.baeza_munoz.PetCare_Caso2.service.ClienteService;
import cl.baeza_munoz.PetCare_Caso2.service.MascotaService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final MascotaService mascotaService;

    public ClienteController(ClienteService clienteService, MascotaService mascotaService) {
        this.clienteService = clienteService;
        this.mascotaService = mascotaService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("clientes", clienteService.listar(q));
        model.addAttribute("q", q);
        return "clientes/lista";
    }

    
    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("cliente", clienteService.obtener(id));
        model.addAttribute("mascotas", mascotaService.listarPorCliente(id));
        return "clientes/detalle";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("cliente", new ClienteDto());
        return "clientes/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("cliente", clienteService.obtener(id));
        return "clientes/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("cliente") ClienteDto cliente, BindingResult result,
                          RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "clientes/form";
        }
        try {
            boolean nuevo = cliente.getId() == null;
            ClienteDto guardado = clienteService.guardar(cliente);
            ra.addFlashAttribute("exito", nuevo
                    ? "Cliente registrado correctamente"
                    : "Cliente actualizado correctamente");
            return "redirect:/clientes/" + guardado.getId();
        } catch (ConflictoException e) {
            result.rejectValue("rut", "rut.conflicto", e.getMessage());
            return "clientes/form";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            clienteService.eliminar(id);
            ra.addFlashAttribute("exito", "Cliente eliminado correctamente");
        } catch (ConflictoException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/clientes";
    }
}
