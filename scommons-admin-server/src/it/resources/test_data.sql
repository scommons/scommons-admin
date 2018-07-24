
--
-- Insert test company
--
INSERT INTO companies(id, name)
    VALUES (1, 'Test Company');

--
-- Insert test user contact info
--
INSERT INTO contacts(id, email, firstname, middlename, phone, secondname, company_id)
    VALUES (1, 'test@test.com', 'test', NULL, NULL, 'test', 1);

--
-- Insert test user (password: test), with all permissions (SUPERUSER role)
--
INSERT INTO users(id, active, created, last_login_date, ldap_domain, login, modified_date, passhash, company_id, contact_id, modifiedby_id)
    VALUES (1, TRUE, '2015-05-28 10:18:00.000', NULL, NULL, 'test', NULL, 'a94a8fe5ccb19ba61c4c0873d391e987982fbbd3', 1, 1, NULL);

--
-- Insert test admin system (application)
--
--INSERT INTO systems(id, name, password, title, url, parent_id)
--    VALUES (1, 'admin', 'admin', 'Common Admin Demo', 'http://localhost:9090/admin.html', NULL);

--
-- Insert admin app test user with all permissions (SUPERUSER role)
--
INSERT INTO systems_users(inherited_roles, modified_date, roles, user_id, system_id, modifiedby_id, parent_id)
    VALUES (NULL, NULL, 1, 1, 1, NULL, NULL);

