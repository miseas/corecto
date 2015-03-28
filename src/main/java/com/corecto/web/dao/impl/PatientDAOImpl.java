/***************************************************************************************
 * No part of this program may be photocopied reproduced or translated to another program
 * language without prior written consent of Matias E. Iseas
 **************************************************************************************/
package com.corecto.web.dao.impl;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.util.CollectionUtils;

import com.corecto.web.dao.PatientDAO;
import com.corecto.web.model.dto.FilterDTO;
import com.corecto.web.model.dto.PacienteDTO;
import com.corecto.web.model.pojo.extra.CatOs;
import com.corecto.web.model.pojo.extra.Consulta;
import com.corecto.web.model.pojo.extra.Paciente;
import com.google.common.base.Joiner;

/**
 * TODO comment<br>
 * <br>
 * <b>Change log:</b>
 * <ul>
 * <li>1.0 20/02/2013 - Xpost - initial version</li>
 * </ul>
 * 
 * @author Xpost
 * @version 1.0
 */
public class PatientDAOImpl extends HibernateDaoSupport implements PatientDAO {

	Logger LOG = LoggerFactory.getLogger(PatientDAOImpl.class);

	public Consulta loadConsultaByIdClient(long idPaciente) throws DataAccessException {
		LOG.info("PatientDAOImpl.loadConsultaByIdClient()");
		List<Consulta> lstResult = getHibernateTemplate().find(
				"select C from Consulta as C where C.paciente.idpaciente='" + idPaciente + "'");

		if (lstResult.isEmpty()) {
			return null;
		} else
			return lstResult.get(0);
	}

	public long saveNewConsulta(Consulta consulta) throws DataAccessException {
		LOG.info("PatientDAOImpl.saveNewPatient()");
		getHibernateTemplate().save(consulta);
		return consulta.getIdconsulta();
	}

	public long saveNewPatient(Paciente paciente) throws DataAccessException {
		LOG.info("PatientDAOImpl.saveNewPatient()");
		getHibernateTemplate().save(paciente);
		return paciente.getIdpaciente();
	}

	public Long checkConsultByPatientId(Long patientId) throws DataAccessException {
		LOG.info("PatientDAOImpl.checkConsultByPatientId()");
		List<Long> lstResult = getHibernateTemplate().find(
				"select C.idconsulta from Consulta as C where C.paciente.idpaciente='" + patientId + "'");
		if (lstResult.isEmpty()) {
			return null;
		} else
			return lstResult.get(0);
	}

	public List<PacienteDTO> getPatientsByParameters(final Date initialDate, final Long patientID,
			final String direction, final String fieldSort, final String sort) throws DataAccessException {

		List<PacienteDTO> listResult = new ArrayList<PacienteDTO>();
		HibernateCallback callback = new HibernateCallback() {

			public Object doInHibernate(Session session) throws HibernateException, SQLException {

				String finalQuery = "";

				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if (patientID != null) {
					finalQuery = " and P.idpaciente=" + patientID;
				}
				// if (initialDate!=null){
				// finalQuery =
				// finalQuery+" and V.inicia>='"+dateFormat.format(initialDate)+"'"
				// ;
				// }
				if (!direction.isEmpty()) {
					finalQuery = finalQuery + " and P.domicilio like '%" + direction + "%'";
				}
				if (!fieldSort.isEmpty()) {
					// if (sort.contains("asc")){
					// criteria.addOrder(Order.asc(fieldSort));
					// }
					// else{
					// criteria.addOrder(Order.desc(fieldSort));
					// }
				}

				Query query = session
						.createQuery("select new com.corecto.web.model.dto.PacienteDTO(P.idpaciente,O.idos,O.nombre,P.nombre,P.sexo,P.fechanac,P.direccion,P.localidad,P.telefono,P.mail,P.nroOs,P.notas,P.dni) "
								+ " FROM Paciente as P, CatOs as O"
								+ " WHERE P.catOs=O.idos "
								+ finalQuery
								+ " order by P.idpaciente");

				List result = query.list();
				return result;
			}
		};

		listResult = (List<PacienteDTO>) this.getHibernateTemplate().execute(callback);
		LOG.info("Cantidad:" + listResult.size());
		return listResult;
	}

	public List<Paciente> getPatientsByName(final String name, final String fieldSort, final String sort,
			final int maxResult) throws DataAccessException {

		List<Paciente> listResult = new ArrayList<Paciente>();
		HibernateCallback callback = new HibernateCallback() {

			public Object doInHibernate(Session session) throws HibernateException, SQLException {

				String orderBy = "C.nombre";
				if (!fieldSort.contentEquals("")) {
					orderBy = "C." + fieldSort;
				}
				Query query = session.createQuery(
						"select C " + " FROM Paciente as C" + " WHERE LOWER(C.nombre) LIKE '%"
								+ name.toLowerCase() + "%' order by " + orderBy + " " + sort).setMaxResults(
						maxResult);

				List result = query.list();
				return result;

			}
		};

		listResult = (List<Paciente>) this.getHibernateTemplate().execute(callback);
		LOG.info("Cantidad:" + listResult.size());
		return listResult;
	}

	@SuppressWarnings("unchecked")
	public List<Paciente> getPatientsByFilter(final FilterDTO filter) throws DataAccessException {

		List<Paciente> listResult = new ArrayList<Paciente>();
		HibernateCallback<Object> callback = new HibernateCallback<Object>() {

			public Object doInHibernate(Session session) throws HibernateException, SQLException {

				StringBuilder queryPatient = new StringBuilder();

				if (StringUtils.isNotBlank(filter.getNombre())) {
					String nombreQ = " and P.nombre like '%" + filter.getNombre() + "%'";
					queryPatient.append(nombreQ);
				}
				if (StringUtils.isNotBlank(filter.getDni())) {
					String dniQ = " and P.dni  like '" + filter.getDni() + "%'";
					queryPatient.append(dniQ);
				}
				if (StringUtils.isNotBlank(filter.getSexo())) {
					String sexoQ = " and P.sexo = '" + filter.getSexo() + "'";
					queryPatient.append(sexoQ);
				}
				// motivo
				StringBuilder queryMotivos = new StringBuilder();
				if (StringUtils.isNotBlank(filter.getConsultaMotivos())) {
					String motivosQ = " and Mo.motivo like '%" + filter.getConsultaMotivos() + "%'";
					queryMotivos.append(motivosQ);
				}
				if (StringUtils.isNotBlank(filter.getConsultaMotivoOtro())) {
					String motivoOtroQ = " and Mo.motivoOtro like '%" + filter.getConsultaMotivoOtro() + "%'";
					queryMotivos.append(motivoOtroQ);
				}
				if (StringUtils.isNotBlank(filter.getFechaInSintoma())) {
					String fechaIniQ = " and Mo.fechaInicio = '" + filter.getFechaInSintoma() + "'";
					queryMotivos.append(fechaIniQ);
				}
				if (StringUtils.isNotBlank(filter.getMotivoEvoMeses())) {
					String evoMesesQ = " and Mo.evoMeses = '" + filter.getMotivoEvoMeses() + "'";
					queryMotivos.append(evoMesesQ);
				}
				StringBuilder otherTables = new StringBuilder();
				StringBuilder otherTablesJoin = new StringBuilder();
				if (queryMotivos.length() > 0) {
					otherTables.append(", Motivo Mo");
					otherTablesJoin.append(" and Mo.consulta.idconsulta = C.idconsulta");
				}
				// antecedentes
				StringBuilder queryAntecedentes = new StringBuilder();
				if (StringUtils.isNotBlank(filter.getPatologicoNinguno())) {
					String patologicoNingunoQ = " and An.patologicoNinguno = "
							+ filter.getPatologicoNinguno();
					queryAntecedentes.append(patologicoNingunoQ);
				}
				if (StringUtils.isNotBlank(filter.getPatologicoAdenoma())) {
					String patologicoNingunoQ = " and An.patologicoAdenoma = "
							+ filter.getPatologicoAdenoma();
					queryAntecedentes.append(patologicoNingunoQ);
				}
				if (StringUtils.isNotBlank(filter.getPatologicoColitis())) {
					String patologicoNingunoQ = " and An.patologicoColitis = "
							+ filter.getPatologicoColitis();
					queryAntecedentes.append(patologicoNingunoQ);
				}
				if (StringUtils.isNotBlank(filter.getPatologicoCrohn())) {
					String patologicoNingunoQ = " and An.patologicoCrohn = " + filter.getPatologicoCrohn();
					queryAntecedentes.append(patologicoNingunoQ);
				}
				if (StringUtils.isNotBlank(filter.getPatologicoHiv())) {
					String patologicoNingunoQ = " and An.patologicoHiv = " + filter.getPatologicoHiv();
					queryAntecedentes.append(patologicoNingunoQ);
				}
				if (StringUtils.isNotBlank(filter.getPatologicoNeoplasia())) {
					String patologicoNingunoQ = " and An.patologicoNeoplasia = "
							+ filter.getPatologicoNeoplasia();
					queryAntecedentes.append(patologicoNingunoQ);
				}
				if (StringUtils.isNotBlank(filter.getNeoplasia())) {
					String neoplasiaQ = " and An.neoplasia like '" + filter.getNeoplasia() + "%'";
					queryAntecedentes.append(neoplasiaQ);
				}
				if (StringUtils.isNotBlank(filter.getFamiliarCancer())) {
					String familarAQ = "";
					String[] familiaCancerTypes = filter.getFamiliarCancer().split("-");
					for (int i = 1; i < familiaCancerTypes.length; i++) {
						familarAQ += "-" + familiaCancerTypes[i];
					}
					familarAQ = " and An.familiarCancer like '%" + familarAQ + "%'";
					queryAntecedentes.append(familarAQ);
				}
				if (!CollectionUtils.isEmpty(filter.getTipoCcrh())) {
					String antCcrhQ = " and An.tipoCcrh in (" + Joiner.on(",").join(filter.getTipoCcrh())
							+ ")";
					queryAntecedentes.append(antCcrhQ);
				}
				if (StringUtils.isNotBlank(filter.getAntecedentesCcrh())) {
					String antecedentesCcrh = " and An.antecedentesCcrh like '%"
							+ filter.getAntecedentesCcrh() + "%'";
					queryAntecedentes.append(antecedentesCcrh);
				}
				if (queryAntecedentes.length() > 0) {
					otherTables.append(", Antecedentes An");
					otherTablesJoin.append(" and An.consulta.idconsulta = C.idconsulta");
				}
				// eva clínica
				StringBuilder queryEvaClinica = new StringBuilder();
				if (StringUtils.isNotBlank(filter.getEvaRecto())) {
					String evaRectoQ = " and Ec.recto like '%" + filter.getEvaRecto() + "%'";
					queryEvaClinica.append(evaRectoQ);
				}
				if (queryEvaClinica.length() > 0) {
					otherTables.append(", EvaClinica Ec");
					otherTablesJoin.append(" and Ec.consulta.idconsulta = C.idconsulta");
				}
				// preconsulta
				StringBuilder queryPreconsulta = new StringBuilder();
				if (StringUtils.isNotBlank(filter.getPeso())) {
					String pesoQ = " and Pe.peso = " + filter.getPeso();
					queryPreconsulta.append(pesoQ);
				}
				if (StringUtils.isNotBlank(filter.getTalla())) {
					String tallaQ = " and Pe.talla = " + filter.getTalla();
					queryPreconsulta.append(tallaQ);
				}
				if (StringUtils.isNotBlank(filter.getSuperficie())) {
					String superficieQ = " and Pe.supcorporal like '%" + filter.getSuperficie() + "%'";
					queryPreconsulta.append(superficieQ);
				}
				if (StringUtils.isNotBlank(filter.getPerformance())) {
					String performanceQ = " and Pe.performanceStatus =  '" + filter.getPerformance() + "'";
					queryPreconsulta.append(performanceQ);
				}
				if (queryPreconsulta.length() > 0) {
					otherTables.append(", Preconsulta Pe");
					otherTablesJoin.append(" and Pe.consulta.idconsulta = C.idconsulta");
				}
				// Examen Proctologico
				StringBuilder queryExaProcto = new StringBuilder();
				if (StringUtils.isNotBlank(filter.getMovilRectal())
						|| StringUtils.isNotBlank(filter.getFijoRectal())) {
					String tactoQ = " and ExPro.tactoRectal like '"
							+ StringUtils.defaultString(filter.getMovilRectal(), "%") + "//"
							+ StringUtils.defaultString(filter.getFijoRectal(), "%") + "'";
					queryExaProcto.append(tactoQ);
				}
				if (StringUtils.isNotBlank(filter.getEsfinterRectal())) {
					String infilTraQ = " and ExPro.tactoRectalInfiltra = '" + filter.getEsfinterRectal()
							+ "'";
					queryExaProcto.append(infilTraQ);
				}
				if (!CollectionUtils.isEmpty(filter.getEe())) {
					String eeQ = " and ExPro.ee in (" + Joiner.on(",").join(filter.getEe()) + ")";
					queryExaProcto.append(eeQ);
				}
				if (!CollectionUtils.isEmpty(filter.getEeTipo())) {
					String eeTipoQ = " and ExPro.eeTipo in (" + Joiner.on(",").join(filter.getEeTipo()) + ")";
					queryExaProcto.append(eeTipoQ);
				}
				if (!CollectionUtils.isEmpty(filter.getEeTipoN())) {
					String eeTipoNQ = " and ExPro.eeTipoN in (" + Joiner.on(",").join(filter.getEeTipoN())
							+ ")";
					queryExaProcto.append(eeTipoNQ);
				}
				if (!CollectionUtils.isEmpty(filter.getEeInfiltra())) {
					String eeInfiltraQ = " and ExPro.eeInfiltra in ("
							+ Joiner.on(",").join(filter.getEeInfiltra()) + ")";
					queryExaProcto.append(eeInfiltraQ);
				}
				if (!CollectionUtils.isEmpty(filter.getEeInfiltraMedida())) {
					String eeInfiltraMedidaQ = " and ExPro.eeInfiltraMedida in ("
							+ Joiner.on(",").join(filter.getEeInfiltraMedida()) + ")";
					queryExaProcto.append(eeInfiltraMedidaQ);
				}
				if (StringUtils.isNotBlank(filter.getEeFecha())) {
					String fechaeeQ = " and ExPro.eeFecha = '" + filter.getEeFecha() + "'";
					queryExaProcto.append(fechaeeQ);
				}
				if (queryExaProcto.length() > 0) {
					otherTables.append(", ExaProcto ExPro");
					otherTablesJoin.append(" and ExPro.consulta.idconsulta = C.idconsulta");
				}
				// estadificacion
				StringBuilder queryEstadificacion = new StringBuilder();
				if (StringUtils.isNotBlank(filter.getRmCentro())) {
					String rmCentroQ = " and Estad.rmCentro like '%" + filter.getRmCentro() + "'";
					queryEstadificacion.append(rmCentroQ);
				}
				if (StringUtils.isNotBlank(filter.getRmFecha())) {
					String getRmFechaQ = " and Estad.rmFecha = '" + filter.getRmFecha() + "'";
					queryEstadificacion.append(getRmFechaQ);
				}
				if (StringUtils.isNotBlank(filter.getRmDistEsfinter())) {
					String rmDistEsfinterQ = " and Estad.rmDistEsfinter like '%" + filter.getRmDistEsfinter()
							+ "'";
					queryEstadificacion.append(rmDistEsfinterQ);
				}
				if (!CollectionUtils.isEmpty(filter.getRmDistAnal())) {
					String rmDistAnalQ = " and Estad.rmDistAnal in ("
							+ Joiner.on(",").join(filter.getRmDistAnal()) + ")";
					queryEstadificacion.append(rmDistAnalQ);
				}
				if (StringUtils.isNotBlank(filter.getRmAltura())) {
					String rmAlturaQ = " and Estad.rmAltura like '%" + filter.getRmAltura() + "'";
					queryEstadificacion.append(rmAlturaQ);
				}
				if (!CollectionUtils.isEmpty(filter.getRmTumor())) {
					String rmTumorQ = " and Estad.rmTumor in (" + Joiner.on(",").join(filter.getRmTumor())
							+ ")";
					queryEstadificacion.append(rmTumorQ);
				}
				if (!CollectionUtils.isEmpty(filter.getRmTumorN())) {
					String rmTumorNQ = " and Estad.rmTumorN in (" + Joiner.on(",").join(filter.getRmTumorN())
							+ ")";
					queryEstadificacion.append(rmTumorNQ);
				}
				if (queryEstadificacion.length() > 0) {
					otherTables.append(", Estadificacion Estad");
					otherTablesJoin.append(" and Estad.consulta.idconsulta = C.idconsulta");
				}
				// anatomia patologica
				StringBuilder queryAnaPatologica = new StringBuilder();
				if (!CollectionUtils.isEmpty(filter.getGradoDif())) {
					String getGradoDifQ = " and Apat.gradoDif in ("
							+ Joiner.on(",").join(filter.getGradoDif()) + ")";
					queryAnaPatologica.append(getGradoDifQ);
				}
				if (queryAnaPatologica.length() > 0) {
					otherTables.append(", AnatomiaPatologica Apat");
					otherTablesJoin.append(" and Apat.consulta.idconsulta = C.idconsulta");
				}
				// tratamiento
				StringBuilder queryTratamiento = new StringBuilder();
				if (StringUtils.isNotBlank(filter.getUdaOndoRadio())
						|| StringUtils.isNotBlank(filter.getUdaOndoOtro())) {
					String udaondoQ = " and Trat.udaondo  like '"
							+ StringUtils.defaultString(filter.getUdaOndoRadio(), "%") + "//%"
							+ StringUtils.defaultString(filter.getUdaOndoOtro(), "%") + "'";
					queryTratamiento.append(udaondoQ);
				}
				if (queryTratamiento.length() > 0) {
					otherTables.append(", Tratamiento Trat");
					otherTablesJoin.append(" and Trat.consulta.idconsulta = C.idconsulta");
				}

				// finally check if we need to add Consulta
				if (otherTables.length() > 0) {
					otherTables.insert(0, ", Consulta C");
					otherTablesJoin.insert(0, " and P.idpaciente = C.paciente.idpaciente");
				}
				// @formatter:off
				String queryString = " SELECT P FROM Paciente P " + otherTables.toString()
						+ " WHERE P.idpaciente is not null " 
						+ otherTablesJoin.toString()
						+ queryPatient.toString()
						+ queryMotivos.toString()
						+ queryAntecedentes.toString()
						+ queryEvaClinica.toString()
						+ queryPreconsulta.toString() 
						+ queryExaProcto.toString() 
						+ queryEstadificacion.toString()
						+ queryAnaPatologica.toString()
						+ queryTratamiento.toString() 
						+ " ORDER BY P.nombre";
				// @formatter:on
				LOG.info(queryString);
				Query query = session.createQuery(queryString);
				return query.list();

			}
		};

		listResult = (List<Paciente>) this.getHibernateTemplate().execute(callback);
		LOG.info("Cantidad:" + listResult.size());
		return listResult;
	}

	// public List<Paciente> getPatientsByFilter(final FilterDTO filter) throws
	// DataAccessException {
	//
	// List<Paciente> listResult = new ArrayList<Paciente>();
	// HibernateCallback callback = new HibernateCallback() {
	//
	// public Object doInHibernate(Session session) throws HibernateException,
	// SQLException {
	//
	// String name = "";
	// String dni = "";
	// if(!filter.getNombre().isEmpty()){
	// name=" LOWER(C.nombre) LIKE '%"+filter.getNombre().toLowerCase()+"%' and";
	// }
	// if(!filter.getDni().isEmpty()){
	// dni=" C.dni LIKE '"+filter.getDni()+"%' and";
	// }
	//
	// String orderBy = "C.nombre";
	// Query query = session.createQuery(
	// "select C "
	// + " FROM Paciente as C" + " WHERE"+name+dni
	// + " C.idpaciente is not null order by "+orderBy);
	//
	// List result = query.list();
	// return result;
	//
	// }
	// };
	//
	// listResult = (List<Paciente>)
	// this.getHibernateTemplate().execute(callback);
	// LOG.info("Cantidad:"+listResult.size());
	// return listResult;
	// }

	@SuppressWarnings("unchecked")
	public Paciente loadPatientById(final Long idPatient) throws DataAccessException {

		List<Paciente> lstResult = getHibernateTemplate().find(
				"select C from Paciente as C where C.idpaciente=" + idPatient);

		if (lstResult.isEmpty()) {
			return null;
		} else
			return lstResult.get(0);
	}

	/*
	 * 
	 * 
	 * 
	 * 
	 * public List<Cliente> getClientsByParameters(final String name,final
	 * String lastName, final String cuit, final String score,final String
	 * fieldSort, final String sort) throws DataAccessException {
	 * 
	 * List<Cliente> listResult = new ArrayList<Cliente>(); HibernateCallback
	 * callback = new HibernateCallback() {
	 * 
	 * public Object doInHibernate(Session session) throws HibernateException,
	 * SQLException {
	 * 
	 * Criteria criteria = session.createCriteria(Cliente.class); if
	 * (name!=null){ criteria.add( Restrictions.like("nombre", name+"%") ); } if
	 * (lastName!=null){ criteria.add( Restrictions.like("apellido",
	 * lastName+"%") ); } if (cuit!=null){ criteria.add( Restrictions.eq(
	 * "cuit", cuit )); } if (score!=null){ criteria.add( Restrictions.eq(
	 * "puntuacion", new Integer(score) )); }
	 * 
	 * if (!fieldSort.isEmpty()){ if (sort.contains("asc")){
	 * criteria.addOrder(Order.asc(fieldSort)); } else{
	 * criteria.addOrder(Order.desc(fieldSort)); } } return criteria.list(); }
	 * };
	 * 
	 * listResult = (List<Cliente>)
	 * this.getHibernateTemplate().execute(callback);
	 * LOG.info("Cantidad:"+listResult.size()); return listResult; }
	 * 
	 * 
	 * @SuppressWarnings("unchecked") public Cliente loadClientByName(final
	 * String name) throws DataAccessException {
	 * 
	 * List<Cliente> lstCliente = getHibernateTemplate().find(
	 * "select C from Cliente as C where C.nombre='" + name + "'");
	 * 
	 * if (lstCliente.isEmpty()) { return null; } else return lstCliente.get(0);
	 * }
	 * 
	 * 
	 * @SuppressWarnings("unchecked") public EstadoCli
	 * loadEstadoClientById(final Integer idEstadoClient) throws
	 * DataAccessException {
	 * 
	 * List<EstadoCli> lstCliente = getHibernateTemplate().find(
	 * "select C from EstadoCli as C where C.idestadocli=" + idEstadoClient);
	 * 
	 * if (lstCliente.isEmpty()) { return null; } else return lstCliente.get(0);
	 * }
	 * 
	 * @SuppressWarnings("unchecked") public Localidad loadLocalidadById(final
	 * Integer idLocalidad) throws DataAccessException {
	 * 
	 * List<Localidad> lstResults = getHibernateTemplate().find(
	 * "select C from Localidad as C where C.idlocalidad=" + idLocalidad);
	 * 
	 * if (lstResults.isEmpty()) { return null; } else return lstResults.get(0);
	 * }
	 * 
	 * @SuppressWarnings("unchecked") public List<Localidad>
	 * loadAllLocalidades() throws DataAccessException {
	 * 
	 * List<Localidad> lstResults = getHibernateTemplate().find(
	 * "select C from Localidad as C");
	 * 
	 * if (lstResults.isEmpty()) { return null; } else return lstResults; }
	 * 
	 * @SuppressWarnings("unchecked") public List<CatDnitipo> loadAllTypeDni()
	 * throws DataAccessException {
	 * 
	 * List<CatDnitipo> lstResults = getHibernateTemplate().find(
	 * "select T from CatDnitipo as T");
	 * 
	 * if (lstResults.isEmpty()) { return null; } else return lstResults; }
	 * 
	 * @SuppressWarnings("unchecked") public List<EstadoCli>
	 * loadAllClientState() throws DataAccessException {
	 * 
	 * List<EstadoCli> lstResults = getHibernateTemplate().find(
	 * "select E from EstadoCli as E");
	 * 
	 * if (lstResults.isEmpty()) { return null; } else return lstResults; }
	 * 
	 * @SuppressWarnings("unchecked") public Integer loadLastClientId() throws
	 * DataAccessException {
	 * 
	 * List<Integer> lstCliente = getHibernateTemplate().find(
	 * "select max(idcliente) FROM Cliente");
	 * 
	 * if (lstCliente.isEmpty()) { return null; } else return lstCliente.get(0);
	 * }
	 * 
	 * @SuppressWarnings("unchecked") public String loadLastClientNum() throws
	 * DataAccessException {
	 * 
	 * List<Cliente> listResult =null;
	 * 
	 * HibernateCallback callback = new HibernateCallback() {
	 * 
	 * public Object doInHibernate(Session session) throws HibernateException,
	 * SQLException {
	 * 
	 * DetachedCriteria maxId = DetachedCriteria.forClass(Cliente.class)
	 * .setProjection( Projections.max("idcliente") ); Criteria criteria =
	 * session.createCriteria(Cliente.class).add(
	 * Property.forName("idcliente").eq(maxId));
	 * 
	 * return criteria.list(); } };
	 * 
	 * listResult = (List<Cliente>)
	 * this.getHibernateTemplate().execute(callback);
	 * 
	 * return listResult.get(0).getNumerocli(); }
	 */

	@SuppressWarnings("unchecked")
	public List<CatOs> loadAllCatOS() throws DataAccessException {
		List<CatOs> lstResults = getHibernateTemplate().find("select E from CatOs as E");

		if (lstResults.isEmpty()) {
			return null;
		} else
			return lstResults;
	}

	public long updatePatient(Paciente paciente) throws DataAccessException {
		LOG.info("PatientDAOImpl.updatePatient()");
		getHibernateTemplate().update(paciente);
		return paciente.getIdpaciente();
	}

	public void deletePatient(Paciente paciente) throws DataAccessException {
		LOG.info("deletePatient()");
		getHibernateTemplate().delete(paciente);
	}
}
