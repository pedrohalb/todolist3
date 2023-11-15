package br.edu.unifalmg.repository;

import br.edu.unifalmg.domain.Chore;

import java.util.List;

public interface ChoreRepository {

    List<Chore> load();

    boolean saveAll(List<Chore> chores);

    boolean save(Chore chore);

}
