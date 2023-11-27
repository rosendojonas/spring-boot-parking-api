insert into users (id, username, password, role)
 values (100, 'jonas@email.com', '$2a$12$fPHqdCZBH2oRDTlXs0.tU.voMwG9by.6d3vFeMhpUGjUldjciVVmy', 'ROLE_ADMIN'),
 (101, 'ana@email.com', '$2a$12$fPHqdCZBH2oRDTlXs0.tU.voMwG9by.6d3vFeMhpUGjUldjciVVmy','ROLE_CUSTOMER'),
  (102, 'bob@email.com', '$2a$12$fPHqdCZBH2oRDTlXs0.tU.voMwG9by.6d3vFeMhpUGjUldjciVVmy', 'ROLE_CUSTOMER'),
  (103, 'toby@email.com', '$2a$12$fPHqdCZBH2oRDTlXs0.tU.voMwG9by.6d3vFeMhpUGjUldjciVVmy', 'ROLE_CUSTOMER');

 insert into customers (id, name, cpf, user_id) values
      (10, 'Ana Silva', '94140627000', 101),
      (20, 'Roberto Gomes', '89097862051', 102);

insert into parking_slots (id, code, status) values
    (100, 'A-01', 'UNAVAILABLE'),
    (200, 'A-02', 'UNAVAILABLE'),
    (300, 'A-03', 'UNAVAILABLE'),
    (400, 'A-04', 'AVAILABLE'),
    (500, 'A-05', 'AVAILABLE');

insert into customers_has_slots (receipt, car_plate, car_brand, car_model, car_color, check_in, customer_id, parking_slot_id) values
    ('20231122-130122', 'FIT-2020', 'FIAT', 'PALIO', 'VERDE', '2023-11-22 13:02:29', 10, 100),
    ('20231122-130322', 'SIE-2020', 'FIAT', 'SIENA', 'AZUL', '2023-11-22 13:05:29', 20, 200),
    ('20231122-130522', 'FIT-2020', 'FIAT', 'PALIO', 'BRANCO', '2023-11-22 13:07:29', 10, 300);
