package br.ufpi.webservice.servidor.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;

import dao.ProdutoDao;
import model.Municipio;
import model.Natureza;
import model.Produto;
import model.UnidadeGestora;
import model.to.MunicipioTO;
import model.to.NaturezaTO;
import model.to.ProdutoTO;
import model.to.UnidadeGestoraTO;

@Path("/service")
public class ServiceController {
	
	private static List<Produto> produtosWeb;
	
	static {
		ProdutoDao obrasDao = new ProdutoDao();
		produtosWeb = obrasDao.buscarProdutos();
	}
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/obras/{exercicio}/{codigoUG}")
	public List<ProdutoTO> obrasPorExercicioUG(@PathParam("exercicio") String exercicio, @PathParam("codigoUG") String codigoUG){
		List<ProdutoTO> obrasTO = new ArrayList<>();
		for (Produto obra : produtosWeb) {
			if(obra.getExercicio() == Integer.valueOf(exercicio).intValue() && obra.getUnidadeGestora().getCodigo().equals(codigoUG)){
				ProdutoTO obraTO = new ProdutoTO();
				obraTO.setTitulo(obra.getTitulo());
				obraTO.setDescricao(obra.getTitulo());
				obraTO.setProcessoLicitatorio(obra.getProcessoLicitatorio());
				obraTO.setExercicio(obra.getExercicio());
				obrasTO.add(obraTO);
			}
		}
		return obrasTO;
	}
	
	@POST
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/obras")
	public List<ProdutoTO> selecionarProdutos (List<ProdutoTO> obrasSelecionadas) {
		Gson gson = new Gson();
		List<ProdutoTO> obrasTOSelecionadas = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		List<Produto> obrasFiltradas = ProdutoDao.filtarProdutosPorExercicio(produtosWeb, obrasSelecionadas.get(0).getExercicio());
		
		for (Produto obra : obrasFiltradas) {
			for(ProdutoTO obraTO : obrasSelecionadas){
				if(obra.getProcessoLicitatorio().equals(obraTO.getProcessoLicitatorio())){
					obraTO.setUnidadeGestoraTO(converterUnidadeGestoraTO(obra.getUnidadeGestora()));
					if(obra.getUnidadeGestora().getUnidadeSuperior() != null){
						String ugSuperiorJson = gson.toJson(converterUnidadeGestoraTO(obra.getUnidadeGestora().getUnidadeSuperior()));
						obraTO.getUnidadeGestoraTO().setUnidadeSuperior(ugSuperiorJson);
					}
					obraTO.setDataOrdemServico(sdf.format(obra.getDataOrdemServico().getTime()));
					obraTO.setDocumentos(obra.getDocumentos());
					obraTO.setExercicio(obra.getExercicio());
					obraTO.setFontesRecurso(obra.getFontesRecurso());
					obraTO.setId(obra.getId());
					obraTO.setLocalizacaoPorCoordenadaUnica(obra.isLocalizacaoPorCoordenadaUnica());
					obraTO.setParametroReferencia(obra.getParametroReferencia());
					obraTO.setPlanilhaLocalizacaoGeoreferencial(obra.getPlanilhaLocalizacaoGeoreferencial());
					obraTO.setProcessoAdministrativo(obra.getProcessoAdministrativo());
					obraTO.setProcessoLicitatorio(obra.getProcessoLicitatorio());
					obrasTOSelecionadas.add(obraTO);
				}
			}
		}
		
		return obrasTOSelecionadas;
	}
	
	private UnidadeGestoraTO converterUnidadeGestoraTO(UnidadeGestora ug){
		UnidadeGestoraTO ugTO = new UnidadeGestoraTO();
		ugTO.setCodigo(ug.getCodigo());
		ugTO.setDescricao(ug.getDescricao());
		ugTO.setEsfera(ug.getEsfera().name());
		ugTO.setId(ug.getId());
		ugTO.setNaturezaTO(this.coverterNaturezaTO(ug.getNatureza()));
		ugTO.setNome(ug.getNome());
		ugTO.setPoder(ug.getPoder().name());
		ugTO.setMunicipioTO(this.converterMunicipioTO(ug.getMunicipio()));
		return ugTO;
	}
	
	private NaturezaTO coverterNaturezaTO (Natureza natureza){
		NaturezaTO naturezaTO = new NaturezaTO();
		naturezaTO.setNome(natureza.getNome());
		naturezaTO.setId(natureza.getId());
		naturezaTO.setAtivo(natureza.isAtivo());
		naturezaTO.setCodigo(natureza.getCodigo());
		return naturezaTO;
	}
	
	private MunicipioTO converterMunicipioTO (Municipio municipio){
		MunicipioTO municipioTO = new MunicipioTO();
		municipioTO.setId(municipio.getId());
		municipioTO.setNome(municipio.getNome());
		return municipioTO;
	}
}
