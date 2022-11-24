/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package general;

import java.io.InputStream;
import java.io.Serializable;

/**
 *
 * @author francisco
 * Clase con la estructura de los archivos adjuntos que se envian por mail
 */
public class AdjuntoMail implements Serializable {
    private String nombreAdjunto;
    private InputStream streamAdjunto;

    public String getNombreAdjunto() {
        return nombreAdjunto;
    }

    public void setNombreAdjunto(String nombreAdjunto) {
        this.nombreAdjunto = nombreAdjunto;
    }

    public InputStream getStreamAdjunto() {
        return streamAdjunto;
    }

    public void setStreamAdjunto(InputStream streamAdjunto) {
        this.streamAdjunto = streamAdjunto;
    }    
}
