SELECT Editions.ISBN
FROM Editions, Books B, Authors
WHERE B.Author_ID = Authors.Author_ID
AND Editions.Book_ID = B.Book_ID
AND Authors.First_Name = 'Agatha'
AND Authors.Last_Name = 'Christie'
ORDER BY ISBN DESC;