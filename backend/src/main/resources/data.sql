-- Insert sample users for testing (passwords are bcrypt encoded)
-- admin123 -> $2a$10$rS1wE3FfzGPbF1X8X9YqB.HHh.HxK2HYOfdyQ.KK9OVXDqKe5HK5W
-- test123 -> $2a$10$teGwH3.8t3z81bo8px6XQOcb8tgb1YCmEyM0gx0URKrF5uQUU4T3K
-- demo123 -> $2a$10$QA1UfCZYoH1yS0hYuqtlJe3gdJ2p.TUHtNZsY2T.6UIyPYPyPFzFy

MERGE INTO app_user (username, password) KEY(username) VALUES 
('admin', '$2a$10$rS1wE3FfzGPbF1X8X9YqB.HHh.HxK2HYOfdyQ.KK9OVXDqKe5HK5W'),
('test', '$2a$10$teGwH3.8t3z81bo8px6XQOcb8tgb1YCmEyM0gx0URKrF5uQUU4T3K'),
('demo', '$2a$10$QA1UfCZYoH1yS0hYuqtlJe3gdJ2p.TUHtNZsY2T.6UIyPYPyPFzFy');

-- Insert sample books
MERGE INTO book (title, author, description, isbn, published_date) KEY(title) VALUES 
('The Great Gatsby', 'F. Scott Fitzgerald', 'A story of decadence and excess.', '9780743273565', '1925-04-10'),
('To Kill a Mockingbird', 'Harper Lee', 'A story of racial injustice.', '9780446310789', '1960-07-11'),
('1984', 'George Orwell', 'A dystopian social science fiction.', '9780451524935', '1949-06-08');
