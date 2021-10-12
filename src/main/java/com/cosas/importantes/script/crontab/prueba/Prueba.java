package com.cosas.importantes.script.crontab.prueba;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import es.chsegura.persistenciageiser.client.AnexoType;
import es.chsegura.persistenciageiser.client.ApunteRegistroType;
import es.chsegura.persistenciageiser.client.AuthenticationType;
import es.chsegura.persistenciageiser.client.CampoType;
import es.chsegura.persistenciageiser.client.FormularioType;
import es.chsegura.persistenciageiser.client.IRegistroWebService;
import es.chsegura.persistenciageiser.client.InteresadoType;
import es.chsegura.persistenciageiser.client.PeticionBusquedaType;
import es.chsegura.persistenciageiser.client.PeticionConsultaType;
import es.chsegura.persistenciageiser.client.RespuestaType;
import es.chsegura.persistenciageiser.client.ResultadoBusquedaType;
import es.chsegura.persistenciageiser.client.ResultadoConsultaType;
import es.chsegura.persistenciageiser.client.SeccionType;
import es.chsegura.persistenciageiser.client.TipoAsientoEnum;
import es.chsegura.persistenciageiser.client.TipoRespuestaEnum;
import es.chsegura.persistenciageiser.client.VersionRegeco;
import es.chsegura.persistenciageiser.connection.Conexion;
import es.chsegura.persistenciageiser.entity.ControlPeticionDiaria;
import es.chsegura.persistenciageiser.service.IClientService;
import es.chsegura.persistenciageiser.servicesoap.RgecoClientFactory;
import es.chsegura.persistenciageiser.servicesoap.RgecoClientFactoryImpl;

@SpringBootApplication
public class Prueba implements Job {

	@Autowired
	@Qualifier("clienteService")
	private IClientService clienteService;
	
	@Autowired
	private Conexion conection;

	@SuppressWarnings("static-access")
	public void execute(JobExecutionContext context) throws JobExecutionException {

		System.out.println("Prueba Geiser");

		try {
		conection.getConexion();
		}catch (Exception e) {
			System.out.println("No se ha podido conectar a la BD");
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
			ControlPeticionDiaria controlPeticionDiaria = new ControlPeticionDiaria();

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
			/*
			 * Calendar c = Calendar.getInstance(); String now = sdf.format(c.getTime());
			 * String now1 = sdf.format(c.getTime());
			 */

			peticionBusquedaType.setTimestampPresentadoDesde("20211004090000");
			// peticionBusquedaType.setTimestampPresentadoHasta("20211008140000");

			//peticionBusquedaType.setTipoAsiento(TipoAsientoEnum.ENTRADA);

			controlPeticionDiaria.setTimestampPresentadoDesde(peticionBusquedaType.getTimestampPresentadoDesde());
			controlPeticionDiaria.setTimestampPresentadoHasta(peticionBusquedaType.getTimestampPresentadoHasta());

			// peticionBusquedaType.setTipoAsiento(TipoAsientoEnum.SALIDA);

			ResultadoBusquedaType resultadoBusquedaType = new ResultadoBusquedaType();
			String uidIterator = null;
			do {
				if (uidIterator == null)
					resultadoBusquedaType = rws.buscar(authentication, peticionBusquedaType);
				else
					resultadoBusquedaType = rws.iterar(authentication, uidIterator);
				
				
				try {
					for (ApunteRegistroType apunte : resultadoBusquedaType.getApuntes()) {

						// Creamos nuevo Apunte.
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

						try {
							for (InteresadoType interesados : apunte.getInteresados()) {

								// Creamos nuevo Interesado
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

								try {
									clienteService.guardarInteresado(interesado);
								} catch (Exception e) {
									System.out.println("Error al guardar el Interesado dentro de su tabla");
									e.printStackTrace();
								}

							}
						} catch (Exception e) {
							System.out.println("No se ha encontrado datos para el Interesado");
							e.printStackTrace();
						}

						// Creamos la Llamada a la PeticionConsulta para Recuperar los Anexos.
						
						PeticionConsultaType peticionConsultaType=new PeticionConsultaType();
						
						peticionConsultaType.setNuRegistro(apunte.getNuRegistro());
						peticionConsultaType.setIncluirContenidoAnexo(true);
						peticionConsultaType.setIncluirJustificante(true);
						peticionConsultaType.setIncluirContenidoAnexoCSV(true);
						
						ResultadoConsultaType rc= rws.consultar(authentication, peticionConsultaType);
						
						if (rc.getRespuesta().getTipo()==TipoRespuestaEnum.OK) {
							try {
								for (AnexoType anexos : rc.getApuntes().get(0).getAnexos()) {
	
									// Creamos nuevo Anexo.
									es.chsegura.persistenciageiser.entity.AnexoType anexo = new es.chsegura.persistenciageiser.entity.AnexoType();
	
									anexo.setNombre(anexos.getNombre());
									anexo.setIdentificador(anexos.getIdentificador());
									anexo.setValidez(anexos.getValidez());
									anexo.setTipoDocumento(anexos.getTipoDocumento());
									anexo.setHash(anexos.getHash());
									anexo.setTipoMime(anexos.getTipoMime());
									anexo.setTamanioFichero(anexos.getTamanioFichero());

									// Dice que este campo debería de recogerse el String en base 64 pero el contenido debe ir a Alfresco
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
	
									try {
										clienteService.guardarAnexo(anexo);
									} catch (Exception e) {
										System.out.println("Error al guardar el anexo dentro de su tabla");
										e.printStackTrace();
									}
								}
							} catch (Exception e) {
								System.out.println("No se ha encontrado datos para el Anexo");
								e.printStackTrace();
							}
						} else {
							// LOG O LO QUE FALLA LA CONSULTA PARA TRAER LOS ANEXOS
						}

						try {
							es.chsegura.persistenciageiser.entity.FormularioType formulario = new es.chsegura.persistenciageiser.entity.FormularioType();
							formulario.setTitulo(apunte.getFormulario().getTitulo());
							formulario.setPlazos(apunte.getPlazos());
							formulario.setSilencioAdministrativo(apunte.getSilencioAdministrativo());

							try {
								clienteService.guardarFormulario(formulario);
							} catch (Exception e) {
								System.out.println("Error al guardar el FormularioType");
								e.printStackTrace();
							}

							try {
								for (SeccionType seccion : apunte.getFormulario().getSecciones()) {
									es.chsegura.persistenciageiser.entity.SeccionType secciones = new es.chsegura.persistenciageiser.entity.SeccionType();

									secciones.setTitulo(seccion.getTitulo());

									for (CampoType campo : apunte.getFormulario().getCampos()) {

										es.chsegura.persistenciageiser.entity.CampoType campos = new es.chsegura.persistenciageiser.entity.CampoType();
										campos.setNombre(campo.getNombre());
										campos.setValor(campo.getValor());

										try {
											clienteService.guardarCampo(campos);
										} catch (Exception e) {
											System.out.println("Error al guardar el CampoType");
											e.printStackTrace();
										}
									}
									try {
										clienteService.guardarSeccion(secciones);
									} catch (Exception e) {
										System.out.println("Error al guardar la SeccionType");
										e.printStackTrace();
									}
								}
							} catch (Exception e) {
								System.out.println("No se ha encontrado datos para la Seccion");
								e.printStackTrace();
							}
						} catch (Exception e) {
							System.out.println("NO se ha encotnrado datos para el Formulario");
							e.printStackTrace();
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

						controlPeticionDiaria.setNuRegistro(apuntes.getNuRegistro());

						try {
							clienteService.guardarApunte(apuntes);
							clienteService.guardarControlPeticion(controlPeticionDiaria);
						} catch (Exception e) {
							System.out.println("Error al guardar los Apuntes/ ControlPeticionDiara en su tabla");
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					System.out.println("Error no hay Apuntes");
					e.printStackTrace();
				}

				try {

					clienteService.guardarControlPeticion(controlPeticionDiaria);
				} catch (Exception e) {
					System.out.println("Error al guardar el ControlPeticionDiaria");
					e.printStackTrace();
				}
				uidIterator = resultadoBusquedaType.getUidIterator();

			} while (uidIterator != null);

			// ver donde almacenar el valor.
			// resultadoBusquedaType.getNuTotalApuntes();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

};
