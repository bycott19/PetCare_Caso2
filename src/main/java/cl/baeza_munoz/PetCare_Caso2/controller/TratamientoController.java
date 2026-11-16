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

import cl.baeza_munoz.PetCare_Caso2.dto.MedicamentoApiDto;
import cl.baeza_munoz.PetCare_Caso2.dto.TratamientoDto;
import cl.baeza_munoz.PetCare_Caso2.exception.ConflictoException;
import cl.baeza_munoz.PetCare_Caso2.exception.RecursoNoEncontradoException;
import cl.baeza_munoz.PetCare_Caso2.service.PetCareApiClient;
import cl.baeza_munoz.PetCare_Caso2.service.ReservaService;
import cl.baeza_munoz.PetCare_Caso2.service.TratamientoService;
import jakarta.validation.Valid;

@Controller
public class TratamientoController {

    private final TratamientoService tratamientoService;
    private final ReservaService reservaService;
    private final PetCareApiClient petCareApiClient;

    public TratamientoController(TratamientoService tratamientoService,
                                 ReservaService reservaService,
                                 PetCareApiClient petCareApiClient) {
        this.tratamientoService = tratamientoService;
        this.reservaService = reservaService;
        this.petCareApiClient = petCareApiClient;
    }

    @GetMapping("/tratamientos")
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("tratamientos", tratamientoService.listar(q));
        model.addAttribute("q", q);
        return "tratamientos/lista";
    }

    @GetMapping("/tratamientos/nuevo")
    public String nuevo(@RequestParam(required = false) Long reservaId, Model model) {
        TratamientoDto tratamiento = new TratamientoDto();
        if (reservaId != null) {
            tratamiento.setReservaId(reservaId);
        }
        model.addAttribute("tratamiento", tratamiento);
        model.addAttribute("reservasDisponibles", reservaService.listarDisponiblesParaTratamiento());
        return "tratamientos/form";
    }

    @GetMapping("/tratamientos/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("tratamiento", tratamientoService.obtener(id));
        return "tratamientos/detalle";
    }

    @PostMapping("/tratamientos/guardar")
    public String guardar(@Valid @ModelAttribute("tratamiento") TratamientoDto tratamiento,
                          BindingResult result,
                          Model model,
                          RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("reservasDisponibles", reservaService.listarDisponiblesParaTratamiento());
            return "tratamientos/form";
        }

        try {
            boolean nuevo = tratamiento.getId() == null;
            TratamientoDto guardado = tratamientoService.guardar(tratamiento);
            ra.addFlashAttribute("exito", nuevo
                    ? "Tratamiento registrado con éxito. La reserva pasó a estado ATENDIDA."
                    : "Tratamiento actualizado correctamente");
            return "redirect:/tratamientos/" + guardado.getId();
        } catch (ConflictoException | RecursoNoEncontradoException e) {
            result.reject(null, e.getMessage());
            model.addAttribute("reservasDisponibles", reservaService.listarDisponiblesParaTratamiento());
            return "tratamientos/form";
        }
    }

    @PostMapping("/tratamientos/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            tratamientoService.eliminar(id);
            ra.addFlashAttribute("exito", "Tratamiento eliminado correctamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tratamientos";
    }

    /**
     * Endpoint proxy para consultar medicamentos a la API de Node
     * Permite al frontend buscar medicamentos fácilmente sin problemas de CORS ni hosts cruzados.
     */
    @GetMapping("/api/medicamentos")
    @ResponseBody
    public List<MedicamentoApiDto> buscarMedicamentos(@RequestParam(required = false) String nombre,
                                                      @RequestParam(required = false) String especie) {
        return petCareApiClient.buscarMedicamentos(nombre, especie);
    }
}
