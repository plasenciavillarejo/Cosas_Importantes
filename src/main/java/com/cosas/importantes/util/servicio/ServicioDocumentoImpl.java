package es.chsegura.persistenciageiser.service;

import java.io.InputStream;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import es.chsegura.integracionAlfresco.api.DocumentProperties;
import es.chsegura.integracionAlfresco.api.DocumentTypeManager;
import es.chsegura.integracionAlfresco.meta.MetaDocumentInputStream;
import es.chsegura.integracionAlfresco.meta.MetaDocumentService;
import es.chsegura.persistenciageiser.entity.AnexoType;
import es.chsegura.persistenciageiser.util.DocumentMap;

@Service("servicioDocumento")
public class ServicioDocumentoImpl implements IServicioDocumento {

	protected static Logger logger = Logger.getLogger(ServicioDocumentoImpl.class);

	@Resource
	private MetaDocumentService metaDocumentService;

	public MetaDocumentService getMetaDocumentService() {
		return metaDocumentService;
	}

	public void setMetaDocumentService(MetaDocumentService metaDocumentService) {
		this.metaDocumentService = metaDocumentService;
	}

	@Resource
	private DocumentTypeManager documentTypeManager;

	public DocumentTypeManager getDocumentTypeManager() {
		return documentTypeManager;
	}

	public void setDocumentTypeManager(DocumentTypeManager documentTypeManager) {
		this.documentTypeManager = documentTypeManager;
	}

	@Resource
	private DocumentMap documentMap;

	public DocumentMap getDocumentMap() {
		return documentMap;
	}

	public void setDocumentMap(DocumentMap documentMap) {
		this.documentMap = documentMap;
	}

	public String subirAlfresco(AnexoType anexo, InputStream file) {
		if (file == null) {
			throw new RuntimeException("No ha seleccionado un fichero a subir.");
		}

		MetaDocumentInputStream metaDocumentInputStream = new MetaDocumentInputStream(anexo.getNombre(), file,
				anexo.getTipoMime(), documentTypeManager.getDocumentType("cmis:document"));
		DocumentProperties documentProperties = metaDocumentInputStream.getDocumentProperties();

//		documentProperties.setProperty(new DocumentProperty<Object>(Constantes.TIPO_ID, documento.getTipoDocumento().getCodigo()));
//		documentProperties.setProperty(new DocumentProperty<Object>(Constantes.DESCRIPCION_ID, documento.getDescripcion()));
//		documentProperties.setProperty(new DocumentProperty<Object>(Constantes.FECHA_DOCUMENTO_ID, documento.getfDocumento().toString()));
//		documentProperties.setProperty(new DocumentProperty<Object>(Constantes.USUARIO_LOGIN_ID, usuario.getLogin()));
//		documentProperties.setProperty(new DocumentProperty<Object>(Constantes.USUARIO_NOMBRE_ID, usuario.getNombre()));

		metaDocumentService.createDocument(documentMap.getPath(anexo), metaDocumentInputStream);

		logger.info(metaDocumentService);
		
		String path = documentMap.getPath(anexo) + "/" + metaDocumentInputStream.getName();
		String idObject = metaDocumentService.getObjectIdByPath(path);

		return  idObject;

	}
}
