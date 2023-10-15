-- init random
math.randomseed(os.time())
-- the request function that will run at each request
request = function() 
   
   url_path = "/inflations/imperative/" .. math.random(1,371)
-- if we want to print the path generated
   -- print(url_path)
-- Return the request object with the current URL path
   return wrk.format("GET", url_path)
end