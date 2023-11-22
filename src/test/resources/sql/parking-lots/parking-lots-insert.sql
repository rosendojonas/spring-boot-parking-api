insert into users (id, username, password, role)
 values (100, 'jonas@email.com', '$2a$12$fPHqdCZBH2oRDTlXs0.tU.voMwG9by.6d3vFeMhpUGjUldjciVVmy', 'ROLE_ADMIN'),
 (101, 'ana@email.com', '$2a$12$fPHqdCZBH2oRDTlXs0.tU.voMwG9by.6d3vFeMhpUGjUldjciVVmy','ROLE_CUSTOMER'),
  (102, 'bob@email.com', '$2a$12$fPHqdCZBH2oRDTlXs0.tU.voMwG9by.6d3vFeMhpUGjUldjciVVmy', 'ROLE_CUSTOMER'),
  (103, 'toby@email.com', '$2a$12$fPHqdCZBH2oRDTlXs0.tU.voMwG9by.6d3vFeMhpUGjUldjciVVmy', 'ROLE_CUSTOMER');

insert into parking_lots (id, code, status)
 values (10, 'A-01', 'AVAILABLE'),
    (20, 'A-02', 'AVAILABLE'),
    (30, 'A-03', 'UNAVAILABLE'),
    (40, 'A-04', 'AVAILABLE');