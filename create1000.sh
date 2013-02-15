for i in `seq 1 1000`
do
    python create_app_bucket_object.py
    echo $i times done
    echo time: `date +%s`
    sleep 5
done
