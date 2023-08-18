package io.github.cainamicael.musicas.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import io.github.cainamicael.musicas.enums.CategoriasEnum;
import io.github.cainamicael.musicas.models.Musicas;
import io.github.cainamicael.musicas.repositories.MusicasRepository;
import io.github.cainamicael.musicas.representations.MusicasDTO;

@Service
public class MusicasService {

	@Autowired
	private MusicasRepository repository;
	
	/*Crud*/
	public ResponseEntity<?> criar(MusicasDTO musicaDTO) {
		Musicas musica = new Musicas(musicaDTO);
		repository.save(musica);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	public List<MusicasDTO> listarTudo() {
		List<Musicas> musicas =  repository.findAll();
		List<MusicasDTO> musicasDTO = musicas.stream().map(x -> new MusicasDTO(x)).toList();
		return musicasDTO;
	}
	
	public MusicasDTO buscarPeloId(Long id) {
		Musicas musica = repository.findById(id).get();
		return new MusicasDTO(musica);
	}
	
	public List<MusicasDTO> listarPelaCategoria(String categoriaStr) {
		CategoriasEnum categoria = CategoriasEnum.valueOf(categoriaStr.toUpperCase());
		List<Musicas> musicas = repository.findByCategoria(categoria);
		List<MusicasDTO> musicasDTO = musicas.stream().map(x -> new MusicasDTO(x)).toList();
		return musicasDTO;
	}
	
	/*Regras específicas*/
	
	//Escolher 2 musicas de adoração e 1 de celebração
	public List<MusicasDTO> musicasIndicadas() {
		List<MusicasDTO> indicadas = new ArrayList<>();
		
		CategoriasEnum adoracao = CategoriasEnum.ADORACAO;
		CategoriasEnum celebracao = CategoriasEnum.CELEBRACAO;
		
		for(int i = 0; i < 2; i++) {
			logicaDaEscolha(adoracao, indicadas);
		}
		
		logicaDaEscolha(celebracao, indicadas);
				
		return indicadas;
	}
	
	public void logicaDaEscolha(CategoriasEnum categoria, List<MusicasDTO> indicadas) {
		Optional<Musicas> optionalMusica = repository.findByCategoriaNotPlayedMusic(categoria.toString());
		
		if(optionalMusica.isPresent()) {
			Musicas musicaEscolhida = optionalMusica.get();
			
			musicaEscolhida.setDataUltimaVezTocada(new Date());
			repository.save(musicaEscolhida);
			
			indicadas.add(new MusicasDTO(musicaEscolhida));
		} else {
			Musicas musicaEscolhida = repository.findByCategoriaPlayedMusicOrderByData(categoria.toString()).get();
			indicadas.add(new MusicasDTO(musicaEscolhida));
			
			musicaEscolhida.setDataUltimaVezTocada(new Date());
			repository.save(musicaEscolhida);
		}	
	}
}