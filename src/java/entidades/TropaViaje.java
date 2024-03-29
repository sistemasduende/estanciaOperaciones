package entidades;
// Generated 10/07/2021 10:15:57 by Hibernate Tools 4.3.1


import java.math.BigDecimal;

/**
 * TropaViaje generated by hbm2java
 */
public class TropaViaje  implements java.io.Serializable {


     private Integer id;
     private Tropa tropa;
     private Viaje viaje;
     private BigDecimal porcAfectadoAnterior;
     private BigDecimal porcAfectado;
     private BigDecimal porUsadoAnterior;
     private BigDecimal porcDisponible;
     private BigDecimal porcIva;
     private BigDecimal valorAfectadoSinIva;
     private BigDecimal valorIva;
     private BigDecimal valorAfectado;

    public TropaViaje() {
    }

    public TropaViaje(Tropa tropa, Viaje viaje, BigDecimal porcAfectado, BigDecimal porcIva, BigDecimal valorAfectadoSinIva, BigDecimal valorIva, BigDecimal valorAfectado) {
        this.tropa = tropa;
        this.viaje = viaje;
        this.porcAfectado = porcAfectado;
        this.porcIva = porcIva;
        this.valorAfectadoSinIva = valorAfectadoSinIva;
        this.valorIva = valorIva;
        this.valorAfectado = valorAfectado;
    }

    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public Tropa getTropa() {
        return this.tropa;
    }
    
    public void setTropa(Tropa tropa) {
        this.tropa = tropa;
    }
    public Viaje getViaje() {
        return this.viaje;
    }
    
    public void setViaje(Viaje viaje) {
        this.viaje = viaje;
    }

    public BigDecimal getPorcAfectadoAnterior() {
        return porcAfectadoAnterior;
    }

    public void setPorcAfectadoAnterior(BigDecimal porcAfectadoAnterior) {
        this.porcAfectadoAnterior = porcAfectadoAnterior;
    }
    
    public BigDecimal getPorcAfectado() {
        return this.porcAfectado;
    }
    
    public void setPorcAfectado(BigDecimal porcAfectado) {
        this.porcAfectado = porcAfectado;
    }

    public BigDecimal getPorUsadoAnterior() {
        return porUsadoAnterior;
    }

    public void setPorUsadoAnterior(BigDecimal porUsadoAnterior) {
        this.porUsadoAnterior = porUsadoAnterior;
    }

    public BigDecimal getPorcDisponible() {
        return porcDisponible;
    }

    public void setPorcDisponible(BigDecimal porcDisponible) {
        this.porcDisponible = porcDisponible;
    }

    public BigDecimal getPorcIva() {
        return porcIva;
    }

    public void setPorcIva(BigDecimal porcIva) {
        this.porcIva = porcIva;
    }

    public BigDecimal getValorAfectadoSinIva() {
        return valorAfectadoSinIva;
    }
    
    public void setValorAfectadoSinIva(BigDecimal valorAfectadoSinIva) {
        this.valorAfectadoSinIva = valorAfectadoSinIva;
    }

    public BigDecimal getValorIva() {
        return valorIva;
    }

    public void setValorIva(BigDecimal valorIva) {
        this.valorIva = valorIva;
    }
    
    public BigDecimal getValorAfectado() {
        return this.valorAfectado;
    }
    
    public void setValorAfectado(BigDecimal valorAfectado) {
        this.valorAfectado = valorAfectado;
    }

}


