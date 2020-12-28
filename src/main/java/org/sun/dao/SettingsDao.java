package org.sun.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.sun.pojo.Setting;

public interface SettingsDao extends JpaRepository<Setting, String>, JpaSpecificationExecutor<Setting> {

    Setting findByKey(String key);
}
