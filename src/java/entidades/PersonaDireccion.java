package entidades;
// Generated 8 oct. 2022 10:23:07 by Hibernate Tools 4.3.1



/**
 * PersonaDireccion generated by hbm2java
 */
public class PersonaDireccion  implements java.io.Serializable {


     private int id;
     private Localidad localidad;
     private Persona persona;
     private Provincia provincia;
     private String cp;
     private String direccion;
     private String telefono;

    public PersonaDireccion() {
    }

	
    public PersonaDireccion(int id, Persona persona, Provincia provincia, String direccion) {
        this.id = id;
        this.persona = persona;
        this.provincia = provincia;
        this.direccion = direccion;
    }
    public PersonaDireccion(int id, Localidad localidad, Persona persona, Provincia provincia, String cp, String direccion, String telefono) {
       this.id = id;
       this.localidad = localidad;
       this.persona = persona;
       this.provincia = provincia;
       this.cp = cp;
       this.direccion = direccion;
       this.telefono = telefono;
    }
   
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    public Localidad getLocalidad() {
        return this.localidad;
    }
    
    public void setLocalidad(Localidad localidad) {
        this.localidad = localidad;
    }
    public Persona getPersona() {
        return this.persona;
    }
    
    public void setPersona(Persona persona) {
        this.persona = persona;
    }
    public Provincia getProvincia() {
        return this.provincia;
    }
    
    public void setProvincia(Provincia provincia) {
        this.provincia = provincia;
    }
    public String getCp() {
        return this.cp;
    }
    
    public void setCp(String cp) {
        this.cp = cp;
    }
    public String getDireccion() {
        return this.direccion;
    }
    
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    public String getTelefono() {
        return this.telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }




}

