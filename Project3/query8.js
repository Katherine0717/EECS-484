// Query 8
// Find the city average friend count per user using MapReduce.

let city_average_friendcount_mapper = function () {
    emit(this.hometown.city, {friends: this.friends.length, count: 1})
    // TODO: Implement the map function
};

let city_average_friendcount_reducer = function (key, values) {
    reducedVal = { friends: 0, count: 0 };
    for (var i = 0; i < values.length; ++i) {
        reducedVal.friends += values[i].friends;
        reducedVal.count += values[i].count;
    }
    return reducedVal;
    // TODO: Implement the reduce function
};

let city_average_friendcount_finalizer = function (key, reduceVal) {
    // We've implemented a simple forwarding finalize function. This implementation
    // is naive: it just forwards the reduceVal to the output collection.
    // TODO: Feel free to change it if needed.
    return reduceVal.friends/reduceVal.count;
};
