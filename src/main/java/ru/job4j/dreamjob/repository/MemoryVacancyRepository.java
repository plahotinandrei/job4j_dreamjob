package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Vacancy;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@ThreadSafe
public class MemoryVacancyRepository implements VacancyRepository {

    private final AtomicInteger nextId = new AtomicInteger();

    private final Map<Integer, Vacancy> vacancies = new ConcurrentHashMap<>();

    private MemoryVacancyRepository() {
        save(new Vacancy(0, "Intern Java Developer", "Высшее образование", LocalDateTime.now()));
        save(new Vacancy(0, "Junior Java Developer", "1 год опыта работы", LocalDateTime.now()));
        save(new Vacancy(0, "Junior+ Java Developer", "1-3 года опыта работы", LocalDateTime.now()));
        save(new Vacancy(0, "Middle Java Developer", "Опыт работы от 3х лет", LocalDateTime.now()));
        save(new Vacancy(0, "Middle+ Java Developer", "Опыт работы от 3х лет", LocalDateTime.now()));
        save(new Vacancy(0, "Senior Java Developer", "Опыт работы от 6х лет", LocalDateTime.now()));
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(nextId.incrementAndGet());
        vacancies.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public boolean deleteById(int id) {
        return vacancies.remove(id) != null;
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return vacancies.computeIfPresent(vacancy.getId(),
                (id, oldVacancy) -> new Vacancy(
                        oldVacancy.getId(), vacancy.getTitle(),
                        vacancy.getDescription(), vacancy.getCreationDate()
                )) != null;
    }

    @Override
    public Optional<Vacancy> findById(int id) {
        return Optional.ofNullable(vacancies.get(id));
    }

    @Override
    public Collection<Vacancy> findAll() {
        return vacancies.values();
    }
}
