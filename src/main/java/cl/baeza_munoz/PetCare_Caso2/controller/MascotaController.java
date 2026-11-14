package cl.baeza_munoz.PetCare_Caso2.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cl.baeza_munoz.PetCare_Caso2.dto.MascotaDto;
import cl.baeza_munoz.PetCare_Caso2.exception.ConflictoException;
import cl.baeza_munoz.PetCare_Caso2.exception.RecursoNoEncontradoException;
import cl.baeza_munoz.PetCare_Caso2.service.ClienteService;
import cl.baeza_munoz.PetCare_Caso2.service.MascotaService;
import jakarta.validation.Valid;

@Controller
public class MascotaController {

    private final MascotaService mascotaService;
    private final ClienteService clienteService;

    public MascotaController(MascotaService mascotaService, ClienteService clienteService) {
        this.mascotaService = mascotaService;
        this.clienteService = clienteService;
    }

    @GetMapping("/mascotas")
    public String listar(@RequestParam(required = false) Long clienteId,
                         @RequestParam(required = false) String q, Model model) {
        model.addAttribute("mascotas", mascotaService.listar(clienteId, q));
        model.addAttribute("clientes", clienteService.listar(null));
        model.addAttribute("clienteId", clienteId);
        model.addAttribute("q", q);
        return "mascotas/lista";
    }

    @GetMapping("/mascotas/nueva")
    public String nueva(@RequestParam(required = false) Long clienteId, Model model) {
        MascotaDto mascota = new MascotaDto();
        mascota.setClienteId(clienteId);
        model.addAttribute("mascota", mascota);
        model.addAttribute("clientes", clienteService.listar(null));
        return "mascotas/form";
    }

    @GetMapping("/mascotas/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("mascota", mascotaService.obtener(id));
        model.addAttribute("clientes", clienteService.listar(null));
        return "mascotas/form";
    }

    @PostMapping("/mascotas/guardar")
    public String guardar(@Valid @ModelAttribute("mascota") MascotaDto mascota, BindingResult result,
                          Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("clientes", clienteService.listar(null));
            return "mascotas/form";
        }
        try {
            boolean nueva = mascota.getId() == null;
            MascotaDto guardada = mascotaService.guardar(mascota);
            ra.addFlashAttribute("exito", nueva
                    ? "Mascota " + guardada.getNombre() + " registrada con éxito"
                    : "Datos de " + guardada.getNombre() + " actualizados");
            return "redirect:/clientes/" + guardada.getClienteId();
        } catch (RecursoNoEncontradoException e) {
            result.rejectValue("clienteId", "cliente.inexistente", e.getMessage());
            model.addAttribute("clientes", clienteService.listar(null));
            return "mascotas/form";
        }
    }

    @PostMapping("/mascotas/{id}/eliminar")
    public String eliminar(@PathVariable Long id, @RequestParam(required = false) String volver,
                           RedirectAttributes ra) {
        try {
            mascotaService.eliminar(id);
            ra.addFlashAttribute("exito", "Mascota eliminada correctamente");
        } catch (ConflictoException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        boolean rutaInterna = volver != null && volver.startsWith("/") && !volver.startsWith("//");
        return "redirect:" + (rutaInterna ? volver : "/mascotas");
    }

    
    @GetMapping("/api/clientes/{clienteId}/mascotas")
    @ResponseBody
    public List<MascotaDto> mascotasDeCliente(@PathVariable Long clienteId) {
        return mascotaService.listarPorCliente(clienteId);
    }
}
