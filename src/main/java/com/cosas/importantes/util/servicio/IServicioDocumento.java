package es.chsegura.persistenciageiser.service;

import java.io.InputStream;

import es.chsegura.persistenciageiser.entity.AnexoType;

public interface IServicioDocumento {
	
	public String subirAlfresco(AnexoType apunte, InputStream file) ;
}
