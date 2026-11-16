package cl.baeza_munoz.PetCare_Caso2.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cl.baeza_munoz.PetCare_Caso2.dto.ReservaDto;
import cl.baeza_munoz.PetCare_Caso2.exception.ConflictoException;
import cl.baeza_munoz.PetCare_Caso2.exception.RecursoNoEncontradoException;
import cl.baeza_munoz.PetCare_Caso2.model.EstadoReserva;
import cl.baeza_munoz.PetCare_Caso2.service.ClienteService;
import cl.baeza_munoz.PetCare_Caso2.service.MascotaService;
import cl.baeza_munoz.PetCare_Caso2.service.ReservaService;
import cl.baeza_munoz.PetCare_Caso2.service.VeterinarioService;
import jakarta.validation.Valid;

@Controller
public class ReservaController {

    private final ReservaService reservaService;
    private final MascotaService mascotaService;
    private final VeterinarioService veterinarioService;
    private final ClienteService clienteService;

    public ReservaController(ReservaService reservaService,
                             MascotaService mascotaService,
                             VeterinarioService veterinarioService,
                             ClienteService clienteService) {
        this.reservaService = reservaService;
        this.mascotaService = mascotaService;
        this.veterinarioService = veterinarioService;
        this.clienteService = clienteService;
    }

    @GetMapping("/reservas")
    public String listar(@RequestParam(required = false) Long veterinarioId,
                         @RequestParam(required = false) EstadoReserva estado,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                         @RequestParam(required = false) String q,
                         Model model) {
        model.addAttribute("reservas", reservaService.listar(veterinarioId, estado, fecha, q));
        model.addAttribute("veterinarios", veterinarioService.listar());
        model.addAttribute("estados", EstadoReserva.values());
        model.addAttribute("veterinarioId", veterinarioId);
        model.addAttribute("estadoFiltro", estado);
        model.addAttribute("fechaFiltro", fecha);
        model.addAttribute("q", q);
        return "reservas/lista";
    }

    @GetMapping("/reservas/nueva")
    public String nueva(@RequestParam(required = false) Long mascotaId,
                        @RequestParam(required = false) Long clienteId,
                        Model model) {
        ReservaDto reserva = new ReservaDto();
        reserva.setMascotaId(mascotaId);
        reserva.setFecha(LocalDate.now());

        model.addAttribute("reserva", reserva);
        cargarListasForm(model, clienteId);
        return "reservas/form";
    }

    @GetMapping("/reservas/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        ReservaDto reserva = reservaService.obtener(id);
        model.addAttribute("reserva", reserva);
        cargarListasForm(model, reserva.getClienteId());
        return "reservas/form";
    }

    @PostMapping("/reservas/guardar")
    public String guardar(@Valid @ModelAttribute("reserva") ReservaDto reserva,
                          BindingResult result,
                          Model model,
                          RedirectAttributes ra) {
        if (result.hasErrors()) {
            cargarListasForm(model, null);
            return "reservas/form";
        }

        try {
            boolean nueva = reserva.getId() == null;
            reservaService.guardar(reserva);
            ra.addFlashAttribute("exito", nueva ? "Hora reservada con éxito" : "Reserva actualizada correctamente");
            return "redirect:/reservas";
        } catch (ConflictoException e) {
            result.rejectValue("hora", "conflicto.hora", e.getMessage());
            cargarListasForm(model, null);
            return "reservas/form";
        } catch (RecursoNoEncontradoException e) {
            result.reject(null, e.getMessage());
            cargarListasForm(model, null);
            return "reservas/form";
        }
    }

    @PostMapping("/reservas/{id}/confirmar")
    public String confirmar(@PathVariable Long id,
                            @RequestParam(required = false) String volver,
                            RedirectAttributes ra) {
        try {
            reservaService.confirmar(id);
            ra.addFlashAttribute("exito", "Reserva confirmada. Se envió la notificación por correo al cliente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:" + (volver != null && volver.startsWith("/") ? volver : "/reservas");
    }

    @PostMapping("/reservas/{id}/cancelar")
    public String cancelar(@PathVariable Long id,
                           @RequestParam(required = false) String volver,
                           RedirectAttributes ra) {
        try {
            reservaService.cancelar(id);
            ra.addFlashAttribute("exito", "Reserva cancelada. Se notificó al cliente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:" + (volver != null && volver.startsWith("/") ? volver : "/reservas");
    }

    @PostMapping("/reservas/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            reservaService.eliminar(id);
            ra.addFlashAttribute("exito", "Reserva eliminada con éxito");
        } catch (ConflictoException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/reservas";
    }

    private void cargarListasForm(Model model, Long clienteId) {
        model.addAttribute("mascotas", mascotaService.listar(clienteId, null));
        model.addAttribute("veterinarios", veterinarioService.listar());
        model.addAttribute("estados", EstadoReserva.values());
        model.addAttribute("clientes", clienteService.listar(null));
    }
}
