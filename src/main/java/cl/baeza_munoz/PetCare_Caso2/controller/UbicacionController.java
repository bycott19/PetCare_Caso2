package cl.baeza_munoz.PetCare_Caso2.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.ModelAndView;

import cl.baeza_munoz.PetCare_Caso2.service.UbicacionService;

@Controller
public class UbicacionController {

    private static final Logger LOG = LoggerFactory.getLogger(UbicacionController.class);

    private final UbicacionService ubicacionService;

    public UbicacionController(UbicacionService ubicacionService) {
        this.ubicacionService = ubicacionService;
    }

    @GetMapping("/ubicacion")
    public ModelAndView ubicacion() {
        ModelAndView pagina = new ModelAndView("ubicacion");
        try {
            pagina.addObject("ubicacion", ubicacionService.obtener());
        } catch (RestClientException error) {
            LOG.warn("No se pudo consultar la ubicación de PetCare: {}", error.getMessage());
            pagina.setStatus(HttpStatus.SERVICE_UNAVAILABLE);
        }
        return pagina;
    }
}
