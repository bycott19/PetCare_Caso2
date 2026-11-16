package cl.baeza_munoz.PetCare_Caso2.dto;

import java.util.ArrayList;
import java.util.List;

public class MedicamentoConsultaResponseDto {

    private int total;
    private List<MedicamentoApiDto> resultados = new ArrayList<>();

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    public List<MedicamentoApiDto> getResultados() { return resultados; }
    public void setResultados(List<MedicamentoApiDto> resultados) { this.resultados = resultados; }
}
