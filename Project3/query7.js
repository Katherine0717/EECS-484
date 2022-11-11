// Query 7
// Find the number of users born in each month using MapReduce

let num_month_mapper = function () {
    emit(this.MOB, {id: 1});
    // emit( this.MOB, {count: 1} );
    // TODO: Implement the map function
};

let num_month_reducer = function (key, values) {
    reducedVal = {id: 0};
    for (i = 0; i < values.length; i++) {
        reducedVal.id += values[i].id;
    }
    return reducedVal;
    // TODO: Implement the reduce function
};

let num_month_finalizer = function (key, reduceVal) {
    // We've implemented a simple forwarding finalize function. This implementation
    // is naive: it just forwards the reduceVal to the output collection.
    // TODO: Feel free to change it if needed.
    return reduceVal.id;
};
