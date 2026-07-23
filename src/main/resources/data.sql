-- Users (idempotent inserts)
INSERT INTO users (name, email, password, role)
SELECT 'Alice', 'alice@example.com', '$2a$10$Yavz/nSEEfXU1URMZ0pjPuvUnDFv00WAwshyWmEeVCXe/cHXMgdzS', 'USER'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'alice@example.com');

INSERT INTO users (name, email, password, role)
SELECT 'Bob', 'bob@example.com', '$2a$10$Yavz/nSEEfXU1URMZ0pjPuvUnDFv00WAwshyWmEeVCXe/cHXMgdzS', 'USER'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'bob@example.com');

INSERT INTO users (name, email, password, role)
SELECT 'Charlie', 'charlie@example.com', '$2a$10$Yavz/nSEEfXU1URMZ0pjPuvUnDFv00WAwshyWmEeVCXe/cHXMgdzS', 'USER'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'charlie@example.com');

INSERT INTO users (name, email, password, role)
SELECT 'Admin', 'admin@example.com', '$2a$10$/r3RMN7xhOFZS2f3RKpVwuv7PayPAwEzgeVEh8aYOZJUYGdZ2/g/K', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@example.com');

INSERT INTO users (name, email, password, role)
SELECT 'Aniket', 'aniket@example.com', '$2a$10$F0bCulZQ2yc0Zeqt0kNPFOjD7WpfmKcES9nxTs42Bgov6iEsZs3Rm', 'USER'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'aniket@example.com');

-- Orders for Alice
INSERT INTO orders (product_name, user_id)
SELECT 'Laptop', u.id
FROM users u
WHERE u.email = 'alice@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM orders o
    WHERE o.user_id = u.id AND o.product_name = 'Laptop'
);

INSERT INTO orders (product_name, user_id)
SELECT 'Mouse', u.id
FROM users u
WHERE u.email = 'alice@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM orders o
    WHERE o.user_id = u.id AND o.product_name = 'Mouse'
);

-- Orders for Bob
INSERT INTO orders (product_name, user_id)
SELECT 'Phone', u.id
FROM users u
WHERE u.email = 'bob@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM orders o
    WHERE o.user_id = u.id AND o.product_name = 'Phone'
);

-- Orders for Charlie
INSERT INTO orders (product_name, user_id)
SELECT 'Headphones', u.id
FROM users u
WHERE u.email = 'charlie@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM orders o
    WHERE o.user_id = u.id AND o.product_name = 'Headphones'
);

-- Orders for Aniket
INSERT INTO orders (product_name, user_id)
SELECT 'Tablet', u.id
FROM users u
WHERE u.email = 'aniket@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM orders o
    WHERE o.user_id = u.id AND o.product_name = 'Tablet'
);

INSERT INTO orders (product_name, user_id)
SELECT 'Keyboard', u.id
FROM users u
WHERE u.email = 'aniket@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM orders o
    WHERE o.user_id = u.id AND o.product_name = 'Keyboard'
);

-- Order for Admin
INSERT INTO orders (product_name, user_id)
SELECT 'Monitor', u.id
FROM users u
WHERE u.email = 'admin@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM orders o
    WHERE o.user_id = u.id AND o.product_name = 'Monitor'
);
