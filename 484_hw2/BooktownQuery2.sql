(SELECT S.Subject
FROM Subjects S)
MINUS
(SELECT S2.Subject
FROM Subjects S2
INNER JOIN Books B ON B.Subject_ID = S2.Subject_ID)
ORDER BY Subject ASC;