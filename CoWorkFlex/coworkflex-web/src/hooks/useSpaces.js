import { useEffect, useState, useCallback } from "react";
import client from "../api/client";

export function useSpaces({ city = "", capacity = "" } = {}) {
  const [spaces, setSpaces] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchSpaces = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const params = {};
      if (city) params.city = city;
      if (capacity) params.capacity = capacity;
      const { data } = await client.get("/spaces", { params });
      setSpaces(data);
    } catch (err) {
      setError(err.response?.data?.message || "Impossible de charger les espaces.");
    } finally {
      setLoading(false);
    }
  }, [city, capacity]);

  useEffect(() => {
    fetchSpaces();
  }, [fetchSpaces]);

  return { spaces, loading, error, refetch: fetchSpaces };
}

export function useSpaceDetail(spaceId) {
  const [space, setSpace] = useState(null);
  const [desks, setDesks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchDetail = useCallback(async () => {
    if (!spaceId) return;
    setLoading(true);
    setError(null);
    try {
      const [spaceRes, desksRes] = await Promise.all([
        client.get(`/spaces/${spaceId}`).catch(() => ({ data: null })),
        client.get(`/spaces/${spaceId}/desks`),
      ]);
      setSpace(spaceRes.data);
      setDesks(desksRes.data);
    } catch (err) {
      setError(err.response?.data?.message || "Impossible de charger cet espace.");
    } finally {
      setLoading(false);
    }
  }, [spaceId]);

  useEffect(() => {
    fetchDetail();
  }, [fetchDetail]);

  return { space, desks, loading, error, refetch: fetchDetail };
}
