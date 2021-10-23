INSERT INTO customer (name, email, mobile_number, create_date)
    VALUES ('Dupa', 'dupa@gmail.com', '123456789', CURDATE());

INSERT INTO accounts (customer_id, account_number, account_type, branch_address, create_date)
    VALUES (1, 186576453, 'Savings', '123 Main Street, New York', CURDATE());