
module CreditUnion.PullIntoDB where

import CreditUnion.Client (login, getAccounts, getPass', logout)
import System.IO (readFile)
import Control.Monad (liftM)
import Test.WebDriver
import Control.Monad.Trans (liftIO)
import Database.HDBC
import Database.HDBC.Sqlite3
import System (getArgs)
import Data.Time.Clock.POSIX
import System.Process (system)

main = do
    args <- getArgs
    db <- connectSqlite3 $ args !! 0
    run db "create table if not exists balance (person text, shareid text, sharename text, when integer, available integer, total integer)" []
    commit db
    password <- getPass'
    questions <- liftM read $ readFile "/home/jcp/ucreditu/questions"
    putStrLn "Running."
    let loop = do
        flip catch (\e -> putStrLn $ "Exception happened: " ++ show e) $ do
            putStrLn "Starting session"
            accounts <- runSession defaultSession defaultCaps $ do
                login "javawizard" questions pass
                accounts <- getAccounts
                logout
                return accounts
            threadDelay $ 3000*1000
            system "pkill -f firefox"
            putStrLn "Inserting into database"
            time <- liftM (floor . realToFrac) getPOSIXTime
            forM_ accounts $ \(shareName, _, number, available, total) -> do
                (run db "insert into balance (person, shareid, sharename, when, available, total) values (?, ?, ?, ?, ?, ?)" $
                    [toSql "javawizard", toSql $ drop 2 $ dropWhile (/= '-') number, toSql shareName, toSql time,
                     toSql (read $ filter (flip elem "0123456789") available :: Integer), toSql (read $ filter (flip elem "0123456789") available :: Integer)])
            commit db 3600*1000*1000
            putStrLn "Done"
        threadDelay (3600*1000*1000)
        loop
    loop
