// Query 2
// Unwind friends and create a collection called 'flat_users' where each document has the following schema:
// {
//   user_id:xxx
//   friends:xxx
// }
// Return nothing.

function unwind_friends(dbname) {
    db = db.getSiblingDB(dbname);
    
    // TODO: unwind friends
    // db.createCollection("flat_users");
    db.users.aggregate( [ { $unwind: {path: "$friends" ,
    includeArrayIndex: "friends"}}, 
    { $project : { user_id : 1 , friends : 1 , _id : 0} }, 
    { $out : "flat_users" } ] );

    return;
}