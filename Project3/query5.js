// // Query 5
// // Find the oldest friend for each user who has a friend. For simplicity,
// // use only year of birth to determine age, if there is a tie, use the
// // one with smallest user_id. You may find query 2 and query 3 helpful.
// // You can create selections if you want. Do not modify users collection.
// // Return a javascript object : key is the user_id and the value is the oldest_friend id.
// // You should return something like this (order does not matter):
// // {user1:userx1, user2:userx2, user3:userx3,...}

function oldest_friend(dbname) {
    db = db.getSiblingDB(dbname);

    let results = {};

    db.users.aggregate([
      {$project: {
          _id:0,
          user_id: 1,
          friends: 1,
          YOB : 1,
      }},
      {$out: "fiends_list"}
  ]);

  db.fiends_list.find().forEach(function(myDoc){
    let arr = myDoc.friends;
    db.fiends_list.updateMany(  
      {user_id: { $in: arr }},
      {$push: { friends: myDoc.user_id }}
      );
  });

  db.fiends_list.find().forEach(function(myDoc1){
      let arr = myDoc1.friends;
      db.fiends_list.find({ user_id: { $in: arr } }).sort(
          {YOB : 1, user_id: 1}
        ).limit(1).forEach( function(myDoc2){
                results[myDoc1.user_id] = myDoc2.user_id;
                // print("1: "+myDoc1.user_id);
                // print("2: "+myDoc2.user_id);
            });
    });

    return results;
}