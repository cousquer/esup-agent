package org.esupportail.esupAgent.web.beans;

import java.text.SimpleDateFormat;

import gouv.education.harpege.webservice.client.dossierRhAdministratif.InformationsContratsDto;
import org.apache.myfaces.custom.tree2.TreeNodeBase;

public class InformationsContratsNode extends TreeNodeBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1636862832136354679L;

	private InformationsContratsDto informationsContratsDto;

	/**
	 * 
	 */

	private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	/**
	 * the type of the node.
	 */
	private static final String NODE_TYPE = "informationsContratDto";

	public InformationsContratsNode(
			InformationsContratsDto informationsContratsDto) {
		super(NODE_TYPE,
				"n\u00B0"+ informationsContratsDto.getNumeroContrat()  + " " + informationsContratsDto.getTypeContratDto().getLibelleLong().toUpperCase() , false);
		
		this.informationsContratsDto = informationsContratsDto;
	}

	/**
	 * @return the informationsContratsDto
	 */
	public InformationsContratsDto getInformationsContratsDto() {
		return informationsContratsDto;
	}

	/**
	 * @param informationsContratsDto
	 *            the informationsContratsDto to set
	 */
	public void setInformationsContratsDto(
			InformationsContratsDto informationsContratsDto) {
		this.informationsContratsDto = informationsContratsDto;
	}

}
