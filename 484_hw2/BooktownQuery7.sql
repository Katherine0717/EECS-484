CREATE VIEW SumofPages AS
SELECT Books.Title AS Titles, SUM(Editions.pages) AS Total_Pages
FROM Books, Editions
WHERE Books.Book_ID = Editions.Book_ID
GROUP BY Books.Title
ORDER BY SUM(Editions.pages) DESC;

SELECT * FROM SumofPages;
DROP VIEW SumofPages;