package webservice.repository;

import org.springframework.data.repository.CrudRepository;
import webservice.models.record.Record;
import webservice.models.user.User;

public interface RecordRepository extends CrudRepository<Record, Long> {
    Iterable<Record> findByUser(User u);
}
