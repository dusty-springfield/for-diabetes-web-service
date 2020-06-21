package webservice.repository;

import org.springframework.data.repository.CrudRepository;
import webservice.models.record.RecordType;

public interface RecordTypeRepository extends CrudRepository<RecordType, Integer> {
}
