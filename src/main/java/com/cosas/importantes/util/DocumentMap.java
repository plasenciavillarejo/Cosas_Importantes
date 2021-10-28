package es.chsegura.persistenciageiser.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.chsegura.persistenciageiser.entity.AnexoType;
import es.chsegura.persistenciageiser.entity.ApunteRegistroType;

public class DocumentMap {
	
	private String inicioRuta;
		
	public String getPath(AnexoType an) {
		
		ApunteRegistroType ap = an.getApunteRegistroType();
	
		SimpleDateFormat formato = new SimpleDateFormat("yyyyMMddHHmmss");
		Date fecha=null;;
		try {
			fecha = formato.parse(ap.getTimestampPresentado());
		} catch (ParseException e) {
		
			e.printStackTrace();
		}
		
		SimpleDateFormat anio = new SimpleDateFormat("yyyy");
		String fechaAnio = anio.format(fecha);
		SimpleDateFormat mes = new SimpleDateFormat("MM");
		String fechaMes = mes.format(fecha);
		SimpleDateFormat dia = new SimpleDateFormat("dd");
		String fechaDia = dia.format(fecha);
		
		return this.inicioRuta + "/" + fechaAnio + "/" + fechaMes + "/" + fechaDia + "/" + ap.getNuRegistro(); 
	}

	public String getInicioRuta() {
		return inicioRuta;
	}

	public void setInicioRuta(String inicioRuta) {
		this.inicioRuta = inicioRuta;
	}
	
	
}
