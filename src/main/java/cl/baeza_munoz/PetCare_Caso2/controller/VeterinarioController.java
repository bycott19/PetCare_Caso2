package cl.baeza_munoz.PetCare_Caso2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cl.baeza_munoz.PetCare_Caso2.dto.VeterinarioDto;
import cl.baeza_munoz.PetCare_Caso2.exception.ConflictoException;
import cl.baeza_munoz.PetCare_Caso2.service.VeterinarioService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/veterinarios")
public class VeterinarioController {

    private final VeterinarioService veterinarioService;

    public VeterinarioController(VeterinarioService veterinarioService) {
        this.veterinarioService = veterinarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("veterinarios", veterinarioService.listar());
        return "veterinarios/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("veterinario", new VeterinarioDto());
        return "veterinarios/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("veterinario", veterinarioService.obtener(id));
        return "veterinarios/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("veterinario") VeterinarioDto veterinario, BindingResult result,
                          RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "veterinarios/form";
        }
        boolean nuevo = veterinario.getId() == null;
        VeterinarioDto guardado = veterinarioService.guardar(veterinario);
        ra.addFlashAttribute("exito", nuevo
                ? "¡Bienvenido/a " + guardado.getNombre() + " al equipo!"
                : "Datos de " + guardado.getNombre() + " actualizados");
        return "redirect:/veterinarios";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            veterinarioService.eliminar(id);
            ra.addFlashAttribute("exito", "Veterinario eliminado correctamente");
        } catch (ConflictoException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/veterinarios";
    }
}
