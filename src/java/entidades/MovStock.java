package entidades;
// Generated 10/07/2021 10:15:57 by Hibernate Tools 4.3.1


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * MovStock generated by hbm2java
 */
public class MovStock  implements java.io.Serializable {


     private Integer id;
     private char tipoMov;
     private Date fecMov;
     private int idUsuario;
     private Integer idComprobanteRel;
     private Character tipoComprobanteRel;
     private String observaciones;
     private Set<MovStockDet> movStockDets = new HashSet<MovStockDet>(0);

    public MovStock() {
    }

	
    public MovStock(char tipoMov, Date fecMov, int idUsuario) {
        this.tipoMov = tipoMov;
        this.fecMov = fecMov;
        this.idUsuario = idUsuario;
    }
    public MovStock(char tipoMov, Date fecMov, int idUsuario, Integer idComprobanteRel, Character tipoComprobanteRel, String observaciones, Set<MovStockDet> movStockDets) {
       this.tipoMov = tipoMov;
       this.fecMov = fecMov;
       this.idUsuario = idUsuario;
       this.idComprobanteRel = idComprobanteRel;
       this.tipoComprobanteRel = tipoComprobanteRel;
       this.observaciones = observaciones;
       this.movStockDets = movStockDets;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public char getTipoMov() {
        return this.tipoMov;
    }
    
    public void setTipoMov(char tipoMov) {
        this.tipoMov = tipoMov;
    }
    public Date getFecMov() {
        return this.fecMov;
    }
    
    public void setFecMov(Date fecMov) {
        this.fecMov = fecMov;
    }
    public int getIdUsuario() {
        return this.idUsuario;
    }
    
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    public Integer getIdComprobanteRel() {
        return this.idComprobanteRel;
    }
    
    public void setIdComprobanteRel(Integer idComprobanteRel) {
        this.idComprobanteRel = idComprobanteRel;
    }
    public Character getTipoComprobanteRel() {
        return this.tipoComprobanteRel;
    }
    
    public void setTipoComprobanteRel(Character tipoComprobanteRel) {
        this.tipoComprobanteRel = tipoComprobanteRel;
    }
    public String getObservaciones() {
        return this.observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    public Set<MovStockDet> getMovStockDets() {
        return this.movStockDets;
    }
    
    public void setMovStockDets(Set<MovStockDet> movStockDets) {
        this.movStockDets = movStockDets;
    }




}

