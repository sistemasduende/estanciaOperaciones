/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package general;

/**
 *
 * @author francisco
 */
public class ResultadoEnvioEmail {
    private boolean isEnvioExitoso;
    private String mensajeError;
    private String mensajeGeneral;

    public boolean isIsEnvioExitoso() {
        return isEnvioExitoso;
    }

    public void setIsEnvioExitoso(boolean isEnvioExitoso) {
        this.isEnvioExitoso = isEnvioExitoso;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }

    public String getMensajeGeneral() {
        return mensajeGeneral;
    }

    public void setMensajeGeneral(String mensajeGeneral) {
        this.mensajeGeneral = mensajeGeneral;
    }
}
