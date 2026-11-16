package cl.baeza_munoz.PetCare_Caso2.dto;

import java.util.ArrayList;
import java.util.List;

public class MedicamentoApiDto {

    private Long id;
    private String nombre;
    private String principioActivo;
    private String tipo;
    private List<String> presentaciones = new ArrayList<>();
    private List<String> especies = new ArrayList<>();
    private String indicaciones;
    private String advertencias;
    private Boolean requiereReceta;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPrincipioActivo() { return principioActivo; }
    public void setPrincipioActivo(String principioActivo) { this.principioActivo = principioActivo; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public List<String> getPresentaciones() { return presentaciones; }
    public void setPresentaciones(List<String> presentaciones) { this.presentaciones = presentaciones; }

    public List<String> getEspecies() { return especies; }
    public void setEspecies(List<String> especies) { this.especies = especies; }

    public String getIndicaciones() { return indicaciones; }
    public void setIndicaciones(String indicaciones) { this.indicaciones = indicaciones; }

    public String getAdvertencias() { return advertencias; }
    public void setAdvertencias(String advertencias) { this.advertencias = advertencias; }

    public Boolean getRequiereReceta() { return requiereReceta; }
    public void setRequiereReceta(Boolean requiereReceta) { this.requiereReceta = requiereReceta; }
}
