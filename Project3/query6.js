// Query 6
// Find the average friend count per user.
// Return a decimal value as the average user friend count of all users in the users collection.

function find_average_friendcount(dbname) {
    db = db.getSiblingDB(dbname);

    // TODO: calculate the average friend count
    let total = 0;
    let friends = 0;
    db.users.find().forEach(function(myDoc1){
        let arr = myDoc1.friends;
        friends = friends + arr.length;
        total = total + 1;
      });

    return friends/total;
}
