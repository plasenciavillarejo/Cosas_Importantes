package com.cosas.importantes.script.sriptSpring.LogMio;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import es.chsegura.persistenciageiser.client.AnexoType;
import es.chsegura.persistenciageiser.client.ApunteRegistroType;
import es.chsegura.persistenciageiser.client.AuthenticationType;
import es.chsegura.persistenciageiser.client.CampoType;
import es.chsegura.persistenciageiser.client.IRegistroWebService;
import es.chsegura.persistenciageiser.client.InteresadoType;
import es.chsegura.persistenciageiser.client.PeticionBusquedaType;
import es.chsegura.persistenciageiser.client.PeticionConsultaType;
import es.chsegura.persistenciageiser.client.ResultadoBusquedaType;
import es.chsegura.persistenciageiser.client.ResultadoConsultaType;
import es.chsegura.persistenciageiser.client.SeccionType;
import es.chsegura.persistenciageiser.client.TipoRespuestaEnum;
import es.chsegura.persistenciageiser.client.VersionRegeco;
import es.chsegura.persistenciageiser.entity.ControlPeticionDiaria;
import es.chsegura.persistenciageiser.servicesoap.RgecoClientFactory;
import es.chsegura.persistenciageiser.servicesoap.RgecoClientFactoryImpl;

@Service
@EnableScheduling
public class ServicioPrueba{

	@Autowired
	private IClientService clientService;
	
	BufferedWriter out = null;
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	//  cron= (Segundos | Minutos | Horas | Día | Mes | Día de la semana)
	// Nota: */10 -> Indica que cada 10 segundos se ejecutará el trigger.
	  					
	 
	//@Scheduled(cron ="0 0 12 * * ?")
	//@Scheduled(cron ="*/10 * * * * *")
	@Scheduled(cron ="0 30 06 * * ?")
	public void probarGeiser() {
		
		Date fecha = new Date();
		SimpleDateFormat fecharFormat = new SimpleDateFormat("EEEE dd 'de' MMMM, yyyy HH:mm:ss");
		String fechaLog = fecharFormat.format(fecha);
		
		try{
			out = new BufferedWriter(new FileWriter("C:\\Log\\log.txt", true));
		}catch (Exception e) {
			e.printStackTrace();
		}
		

		try {
			RgecoClientFactoryImpl rfi = new RgecoClientFactoryImpl();
			rfi.setAddress("https://rgecopruebas.preappjava.seap.minhap.es/rgeco/services/RegistroWebService?wsdl");
			RgecoClientFactory rf = rfi;
			IRegistroWebService rws = rf.getServicioRgeco();

			AuthenticationType authentication = new AuthenticationType();
			authentication.setAplicacion("AQUO_CHSEG");
			authentication.setUsuario("AQUO_CHSEG");
			authentication.setPassword("AQUO_PRE");
			authentication.setCdAmbito("E03154103");
			authentication.setVersion(VersionRegeco.V_1);

			PeticionBusquedaType peticionBusquedaType = new PeticionBusquedaType();

			 peticionBusquedaType.setTimestampPresentadoDesde("20211001090000");
			 peticionBusquedaType.setTimestampPresentadoHasta("20211016140000");

			//peticionBusquedaType.setTipoAsiento(TipoAsientoEnum.SALIDA);

			ResultadoBusquedaType resultadoBusquedaType = new ResultadoBusquedaType();
			String uidIterator = null;

			do {
				if (uidIterator == null)
					resultadoBusquedaType = rws.buscar(authentication, peticionBusquedaType);
				else
					resultadoBusquedaType = rws.iterar(authentication, uidIterator);

				try {
					out.write("\n				Inicio de la Ejecución para el día:  "       + fechaLog + " \n");
					for (ApunteRegistroType apunte : resultadoBusquedaType.getApuntes()) {

						es.chsegura.persistenciageiser.entity.ApunteRegistroType apuntes = new es.chsegura.persistenciageiser.entity.ApunteRegistroType();

						apuntes.setNuRegistro(apunte.getNuRegistro());
						apuntes.setTimestampPresentado(apunte.getTimestampPresentado());
						apuntes.setTimestampRegistrado(apunte.getTimestampRegistrado());
						apuntes.setJustificanteFirmado(apunte.getJustificanteFirmado());
						apuntes.setTamanioJustificanteFirmado(apunte.getTamanioJustificanteFirmado());
						apuntes.setHashJustificanteFirmado(apunte.getHashJustificanteFirmado());
						apuntes.setTipoMimeJustificanteFirmado(apunte.getTipoMimeJustificanteFirmado());
						apuntes.setJustificanteCSV(apunte.getJustificanteCSV());
						apuntes.setTamanioJustificanteCVS(apunte.getTamanioJustificanteCVS());
						apuntes.setHashJustificanteCVS(apunte.getHashJustificanteCVS());
						apuntes.setTipoMimeJustificanteCVS(apunte.getTipoMimeJustificanteCVS());
						apuntes.setTieneFirmaJustificanteCSV(apunte.isTieneFirmaJustificanteCSV());
						apuntes.setCsv(apunte.getCsv());
						apuntes.setCdAmbitoCreacion(apunte.getCdAmbitoCreacion());
						apuntes.setAmbitoCreacion(apunte.getAmbitoCreacion());
						apuntes.setCdAmbitoActual(apunte.getCdAmbitoActual());
						apuntes.setAmbitoActual(apunte.getAmbitoActual());
						apuntes.setTipoAsiento(apunte.getTipoAsiento());
						apuntes.setEstado(apunte.getEstado());
						apuntes.setCdOrganoOrigen(apunte.getCdOrganoOrigen());
						apuntes.setOrganoOrigen(apunte.getOrganoOrigen());
						apuntes.setCdOrganoDestino(apunte.getCdOrganoDestino());
						apuntes.setOrganoDestino(apunte.getOrganoDestino());

						if (apunte.getInteresados().isEmpty()) {
							log.info("No se ha encontrado datos para la Entidad InteresadoType ");
							
						} else {
							for (InteresadoType interesados : apunte.getInteresados()) {

								es.chsegura.persistenciageiser.entity.InteresadoType interesado = new es.chsegura.persistenciageiser.entity.InteresadoType();

								interesado.setTipoIdentificadorInteresado(interesados.getTipoIdentificadorInteresado());
								interesado.setIdentificadorInteresado(interesados.getIdentificadorInteresado());
								interesado.setNombreInteresado(interesados.getNombreInteresado());
								interesado.setPrimerApellidoInteresado(interesados.getPrimerApellidoInteresado());
								interesado.setSegundoApellidoInteresado(interesados.getSegundoApellidoInteresado());
								interesado.setRazonSocialInteresado(interesados.getRazonSocialInteresado());
								interesado.setCdPaisInteresado(interesados.getCdPaisInteresado());
								interesado.setCdProvinciaInteresado(interesados.getCdProvinciaInteresado());
								interesado.setCdMunicipioInteresado(interesados.getCdMunicipioInteresado());
								interesado.setDireccionInteresado(interesados.getDireccionInteresado());
								interesado.setCodigoPostalInteresado(interesados.getCodigoPostalInteresado());
								interesado.setMailInteresado(interesados.getMailInteresado());
								interesado.setTelefonoInteresado(interesados.getTelefonoInteresado());
								interesado.setCanalNotificacionInteresado(interesados.getCanalNotificacionInteresado());
								interesado.setDireccionElectronicaInteresado(
										interesados.getDireccionElectronicaInteresado());
								interesado.setTipoIdentificadorRepresentante(
										interesados.getTipoIdentificadorRepresentante());
								interesado.setIdentificadorRepresentante(interesados.getIdentificadorRepresentante());
								interesado.setNombreRepresentante(interesados.getNombreRepresentante());
								interesado.setPrimerApellidoRepresentante(interesados.getPrimerApellidoRepresentante());
								interesado
										.setSegundoApellidoRepresentante(interesados.getSegundoApellidoRepresentante());
								interesado.setRazonSocialRepresentante(interesados.getRazonSocialRepresentante());
								interesado.setCdPaisRepresentante(interesados.getCdPaisRepresentante());
								interesado.setCdProvinciaRepresentante(interesados.getCdProvinciaRepresentante());
								interesado.setCdMunicipioRepresentante(interesados.getCdMunicipioRepresentante());
								interesado.setDireccionRepresentante(interesados.getDireccionRepresentante());
								interesado.setCodigoPostalRepresentante(interesados.getCodigoPostalRepresentante());
								interesado.setMailRepresentante(interesados.getMailRepresentante());
								interesado.setTelefonoRepresentante(interesados.getTelefonoRepresentante());
								interesado.setCanalNotificacionRepresentante(
										interesados.getCanalNotificacionRepresentante());
								interesado.setObservaciones(interesados.getObservaciones());
								interesado.setApunteRegistroType(apuntes);

								try {
									clientService.guardarInteresado(interesado);
									log.info("Los datos se han registrado en la Entidad IntersadoType exitosamente.");
									out.write(fechaLog + "- INFO - " + "Los datos se han registrado en la Entidad IntersadoType exitosamente.\n" );
									
								} catch (Exception e) {
									log.error("Error al guardar los datos en la Entidad InteresadoType ");
									out.write(fechaLog + "- ERROR - " +"Error al guardar los datos en la Entidad InteresadoType.\n");
									e.printStackTrace();
								}
							}
						}

						PeticionConsultaType peticionConsultaType = new PeticionConsultaType();

						peticionConsultaType.setNuRegistro(apunte.getNuRegistro());
						peticionConsultaType.setIncluirContenidoAnexo(true);
						peticionConsultaType.setIncluirJustificante(true);
						peticionConsultaType.setIncluirContenidoAnexoCSV(true);

						ResultadoConsultaType rc = rws.consultar(authentication, peticionConsultaType);

						if (rc.getRespuesta().getTipo() == TipoRespuestaEnum.OK) {
							try {
								for (AnexoType anexos : rc.getApuntes().get(0).getAnexos()) {

									es.chsegura.persistenciageiser.entity.AnexoType anexo = new es.chsegura.persistenciageiser.entity.AnexoType();

									anexo.setNombre(anexos.getNombre());
									anexo.setIdentificador(anexos.getIdentificador());
									anexo.setValidez(anexos.getValidez());
									anexo.setTipoDocumento(anexos.getTipoDocumento());
									anexo.setHash(anexos.getHash());
									anexo.setTipoMime(anexos.getTipoMime());
									anexo.setTamanioFichero(anexos.getTamanioFichero());

									// Debería de recogerse el String en base 64 pero el contenido debe ir a
									// Alfresco
									anexo.setAnexo(anexos.getAnexo()); // El contenido debe ir a alfresco

									anexo.setTipoFirma(anexos.getTipoFirma());
									anexo.setNombreFirma(anexos.getNombreFirma());
									anexo.setHashFirma(anexos.getHashFirma());
									anexo.setTipoMimeFirma(anexos.getTipoMimeFirma());
									anexo.setTamanioFicheroFirma(anexos.getTamanioFicheroFirma());
									anexo.setFirma(anexos.getFirma());
									anexo.setAnexoCSV(anexos.getAnexoCSV());
									anexo.setNombreCSV(anexos.getNombreCSV());
									anexo.setHashCSV(anexos.getHashCSV());
									anexo.setTipomimeCSV(anexos.getTipomimeCSV());
									anexo.setTamanioFicheroCSV(anexos.getTamanioFicheroCSV());
									anexo.setCodigoCSV(anexos.getCodigoCSV());
									anexo.setObservaciones(anexos.getObservaciones());
									anexo.setApunteRegistroType(apuntes);

									try {
										clientService.guardarAnexo(anexo);
										log.info("Los datos se han registrado en la Entidad AnexoType exitosamente");
										out.write(fechaLog + "- INFO - " +"Los datos se han registrado en la Entidad AnexoType exitosamente.\n");
										
									} catch (Exception e) {
										log.error("Error al guardar los datos en la Entidad AnexoType.");
										out.write(fechaLog + "- ERROR - " +"Error al guardar los datos en la Entidad AnexoType.\n");
										e.printStackTrace();
									}
								}
							} catch (Exception e) {
								log.info("No se ha encontrado datos para la Entidad AnexoType.");
								out.write(fechaLog + "- ERROR - " + "No se ha encontrado datos para la Entidad AnexoType.\n");
								e.printStackTrace();
							}
						} else {
							// LOG O LO QUE FALLA LA CONSULTA PARA TRAER LOS ANEXOS
							log.info("No se ha encontrado respuesta para -> TipoRespuestaEnum.OK. ");
							out.write(fechaLog + "- ERROR - " + "No se ha encontrado respuesta para -> TipoRespuestaEnum.OK.\n");
						}

						es.chsegura.persistenciageiser.entity.FormularioType formulario = new es.chsegura.persistenciageiser.entity.FormularioType();
						if (apunte.getFormulario() != null) {
							formulario.setTitulo(apunte.getFormulario().getTitulo());
							formulario.setPlazos(apunte.getPlazos());
							formulario.setSilencioAdministrativo(apunte.getSilencioAdministrativo());
							formulario.setApunteRegistroType(apuntes);

							try {
								clientService.guardarFormulario(formulario);
								log.info("Los datos se han registrado en la Entidad FormularioType exitosamente.");
								out.write(fechaLog + "- INFO - " +"Los datos se han registrado en la Entidad FormularioType exitosamente.\n");
							} catch (Exception e) {
								log.error("Error al guardar los datos en la Entidad FormularioType.");
								out.write(fechaLog + "- ERROR - " + "Error al guardar los datos en la Entidad FormularioType.\n");
								e.printStackTrace();
							}

							if (apunte.getFormulario().getSecciones() != null) {
								for (SeccionType seccion : apunte.getFormulario().getSecciones()) {
									es.chsegura.persistenciageiser.entity.SeccionType secciones = new es.chsegura.persistenciageiser.entity.SeccionType();

									secciones.setTitulo(seccion.getTitulo());
									secciones.setFormularioType(formulario);

									if (apunte.getFormulario().getCampos() != null) {
										for (CampoType campo : apunte.getFormulario().getCampos()) {

											es.chsegura.persistenciageiser.entity.CampoType campos = new es.chsegura.persistenciageiser.entity.CampoType();
											campos.setNombre(campo.getNombre());
											campos.setValor(campo.getValor());
											campos.setFormularioType(formulario);
											campos.setSeccionType(secciones);

											try {
												clientService.guardarCampo(campos);
												log.info("Los datos se han registrado en la Entidad CampoType exitosamente. ");
												out.write(fechaLog + "- INFO - " +"Los datos se han registrado en la Entidad CampoType exitosamente. "); 
											} catch (Exception e) {
												log.error("Error al guardar los datos en la Entidad CampoType.");
												out.write(fechaLog + "- ERROR - " + "Error al guardar los datos en la Entidad CampoType.");
												e.printStackTrace();
											}
										}
									}
									try {
										log.info("Los datos se han registrado en la Entidad SeccionesType exitosamente.");
										out.write(fechaLog + "- INFO - " +"Los datos se han registrado en la Entidad SeccionesType exitosamente.");
									} catch (Exception e) {
										log.error("Error al guardar los datos en la Entidad SeccionType. ");
										out.write(fechaLog + "- ERROR - " + "Error al guardar los datos en la Entidad SeccionType. "); 
										e.printStackTrace();
									}
								}
							}
						}
						apuntes.setResumen(apunte.getResumen());
						apuntes.setCdAsunto(apunte.getCdAsunto());
						apuntes.setDeAsunto(apunte.getDeAsunto());
						apuntes.setCdSIA(apunte.getCdSIA());
						apuntes.setCdFormulario(apunte.getCdFormulario());
						apuntes.setDeFormulario(apunte.getDeFormulario());
						apuntes.setReferenciaExterna(apunte.getReferenciaExterna());
						apuntes.setNuExpediente(apunte.getNuExpediente());
						apuntes.setTipoTransporte(apunte.getTipoTransporte());
						apuntes.setNuTransporte(apunte.getNuTransporte());
						apuntes.setNombreUsuario(apunte.getNombreUsuario());
						apuntes.setContactoUsuario(apunte.getContactoUsuario());
						apuntes.setDocumentacionFisica(apunte.getDocumentacionFisica());
						apuntes.setObservaciones(apunte.getObservaciones());
						apuntes.setExpone(apunte.getExpone());
						apuntes.setSolicita(apunte.getSolicita());
						apuntes.setAsuntoInterno(apunte.getAsuntoInterno());
						apuntes.setCodigoCadenaAsientos(apunte.getCodigoCadenaAsientos());
						apuntes.setTimestampFactura(apunte.getTimestampFactura());
						apuntes.setNuRegistroInterno(apunte.getNuRegistroInterno());
						apuntes.setNuRegistroOrigen(apunte.getNuRegistroOrigen());
						apuntes.setImporteFactura(apunte.getImporteFactura());
						apuntes.setNumeroFactura(apunte.getNumeroFactura());
						apuntes.setJustificanteUnidadTramitacion(apunte.getJustificanteUnidadTramitacion());
						apuntes.setCdTipodocumento(apunte.getCdTipodocumento());
						apuntes.setDeTipodocumento(apunte.getDeTipodocumento());
						apuntes.setCdZonaHorariaCreacion(apunte.getCdZonaHorariaCreacion());
						apuntes.setDeZonaHorariaCreacion(apunte.getDeZonaHorariaCreacion());
						apuntes.setCdZonaHorariaUsuario(apunte.getCdZonaHorariaUsuario());
						apuntes.setDeZonaHorariaUsuario(apunte.getDeZonaHorariaUsuario());
						apuntes.setFormularioType(formulario);

						try {
							clientService.guardarApunte(apuntes);
							log.info("Los datos se han registrado en la Entidad ApuntesRegistroType exitosamente.");
							out.write(fechaLog + "- INFO - " +"Los datos se han registrado en la Entidad ApuntesRegistroType exitosamente.\n");
						} catch (Exception e) {
							log.error("Error al guardar los datos en la Entidad ApuntesRegistroTypeType.");
							out.write(fechaLog + "- ERROR - " + "Error al guardar los datos en la Entidad ApuntesRegistroTypeType.\n");
							e.printStackTrace();
						}

						try {
							
							ControlPeticionDiaria controlPeticionDiaria = new ControlPeticionDiaria();
								
								controlPeticionDiaria.setNuRegistro(apuntes.getNuRegistro());
								controlPeticionDiaria.setApunteRegistroType(apuntes);
								controlPeticionDiaria.setTimestampPresentadoDesde(peticionBusquedaType.getTimestampPresentadoDesde());
								controlPeticionDiaria.setTimestampPresentadoHasta(peticionBusquedaType.getTimestampPresentadoHasta());
								
							clientService.guardarControlPeticion(controlPeticionDiaria);
							log.info("Los datos se han registrado en la Entidad ControlPeticionDiaria exitosamente.");
							out.write(fechaLog + "- INFO - " + "Los datos se han registrado en la Entidad ControlPeticionDiaria exitosamente.\n");
							
						} catch (Exception e) {
							log.error("Error al guardar los datos en la Entidad ControlPeticionDiaria.");
							out.write(fechaLog + "- ERROR - " +"Error al guardar los datos en la Entidad ControlPeticionDiaria.\n");
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					log.error("No hay datos para la Entidad ApuntesRegistroType.");
					out.write(fechaLog + "- ERROR - " +"No hay datos para la Entidad ApuntesRegistroType.\n"); 
					e.printStackTrace();
				}

				uidIterator = resultadoBusquedaType.getUidIterator();

			} while (uidIterator != null);

			// ver donde almacenar el valor.
			// resultadoBusquedaType.getNuTotalApuntes();
			try {
				log.info("El número total de apuntes recuperados son: " + resultadoBusquedaType.getNuTotalApuntes()
						+ " apuntes.");
				out.write(fechaLog + "- INFO - " + "El número total de apuntes recuperados son: " + resultadoBusquedaType.getNuTotalApuntes()
				+ " apuntes.\n");
				
				out.write("				Fin de la Ejecución para el día:  "       + fechaLog + " \n\n");
				out.close();
			} catch (Exception e) {
				log.error("Error al recuperar el Nº total de apuntes.");
				out.write(fechaLog + "- ERROR - " + "Error al recuperar el Nº total de apuntes.");
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

};
