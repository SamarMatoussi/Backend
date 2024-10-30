package tn.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.Backend.entites.Kpi;

import java.awt.*;
import java.util.List;

@Repository
public interface KpiRepository extends JpaRepository<Kpi, Long> {
    List<Kpi> findAllByActivitesId(Long activiteId);
   /* @Query("SELECT o.optionValue FROM KpiOption o WHERE o.kpi.id = :kpiId")
    List<String> findOptionsByKpiId(@Param("kpiId") Long kpiId);*/
}
